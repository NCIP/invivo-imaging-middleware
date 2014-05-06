/**
 *
 */
package gov.nih.nci.ivi.dicom;

import gov.nih.nci.ivi.dicom.modelmap.ModelDictionary;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.ValueRepresentation;

/**
 * @author tpan
 * 
 *         assume the target method on the models are of the form
 *         "void setXXX(YYY xxx)" restriction on the object tree generated -
 *         starting from the target class, traversal only up, there is no
 *         retrograde traversal (back down). This means that a class's sibling
 *         not of the same type are not created. e.g. A is parent of B and C,
 *         and C is target class, then A will be created, but not B.
 * 
 *         some of the axis objects may not print correctly - toString generates
 *         a "null"
 * 
 */
public class DICOM2Model {
    private static final Logger myLogger = LogManager.getLogger(DICOM2Model.class);

    private ModelMap map;
    private ModelDictionary mDictionary;
    // private HashMap<String, Object> patientIdList;
    public boolean debug = false;
    public boolean debug2 = false;
    public boolean production = false;
    public boolean debug3 = false;
    public boolean debug4 = false;
    // using possessive quantifier - match only once.
    // see DICOM Part 5 for definition.
    // dicom date: yyyyMMdd
    private static final Pattern datePattern = Pattern.compile("\\d{4}+([0-1]\\d){1}+([0-3]\\d){1}+");
    // dicom time: HH[mm[ss[.F[F[F[F[F[F]]]]]]]]
    private static final Pattern timePattern = Pattern
            .compile("([0-2]\\d){1}+([0-5]\\d)?+([0-5]\\d)?+(\\.{1}+\\d{1,6}+)?+");
    // dicom datetime: yyyy[MM[dd[hh[mm[ss[.F[F[F[F[F[F]]]]]]]]]]][&ZZ[XX]]
    private static final Pattern dateTimePattern = Pattern
            .compile("\\d{4}+([0-1]\\d)?+([0-3]\\d)?+([0-2]\\d)?+([0-5]\\d)?+([0-5]\\d)?+(\\.{1}+\\d{1,6}+)?+([\\+-][0-2][0-9]([0-5][0-9])?+)?+");

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
    // private static final SimpleDateFormat timeFormatter = new
    // SimpleDateFormat("HHmmss.SSS");
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyyMMddHHmmss.SSSZ");

    /**
     * convert from dicom's date string format, which is either yyyyMMdd or
     * yyyy.MM.dd or yyyy/MM/dd to the v3.0 standard yyyyMMdd. then use the
     * dateFormatter within the conversion tool to get the date object
     */
    static String convertDateString(String dicomDateStr) throws DataConversionException {
        if (dicomDateStr == null) {
            return null;
        }
        if (dicomDateStr.equals("")) {
            return null;
        }

        String output = dicomDateStr.replaceAll("\\.", "");
        output = output.replaceAll("/", "");

        // check to make sure we are working with mostly integers
        Matcher m = datePattern.matcher(output);
        if (!m.matches()) {
            throw new DataConversionException("malformed date string: " + dicomDateStr);
        }

        return output;
    }

    /**
     * convert from dicom's time string format, which is either HHmmss.SSSSSS or
     * HH:mm:ss.SSSSSS to a form that can be understood by
     * org.apache.axis.types.Time, which is HH:mm:ss[.SSS] note that in dicom,
     * fields to the right are optional
     * 
     * @param dicomTimeStr
     * @return
     */
    static String convertTimeString(String dicomTimeStr) throws DataConversionException {
        if (dicomTimeStr == null) {
            return null;
        }
        if (dicomTimeStr.equals("")) {
            return null;
        }

        String newstr = dicomTimeStr.replaceAll(":", "");

        // check to make sure we are working with mostly integers
        Matcher m = timePattern.matcher(newstr);
        if (!m.matches()) {
            throw new DataConversionException("malformed time string: " + dicomTimeStr);
        }

        // TODO: can convert this to use reg ex later.

        // hour
        int pos = 0;
        int len = 2;
        String HH = dicomTimeStr;
        if (newstr.length() >= pos + len) {
            HH = newstr.substring(pos, pos + len);
        }
        pos += len;

        // minute
        String mm = "00";
        if (newstr.length() >= pos + len) {
            mm = newstr.substring(pos, pos + len);
        }
        pos += len;

        // second
        String ss = "00";
        if (newstr.length() >= pos + len) {
            ss = newstr.substring(pos, pos + len);
        }
        pos += len;

        // microsec
        int SSS = 1000;
        if (newstr.length() >= pos + len) {
            SSS = Math.round(Float.parseFloat(newstr.substring(pos)) * 1000.0f) + SSS;
        }

        return HH + mm + ss + "." + Integer.toString(SSS).substring(1);
    }

    /**
     * convert datetime string to the proper format understood by the
     * simpleDateFormat DICOM format is yyyyMMddHHmmss.SSSSSS&ZZZZ, where & is
     * +/- and ZZZZ are hour and minute for timezone.
     * 
     * @param dicomDateTimeStr
     * @return
     */
    static String convertDateTimeString(String dicomDateTimeStr) throws DataConversionException {
        if (dicomDateTimeStr == null) {
            return null;
        }
        if (dicomDateTimeStr.equals("")) {
            return null;
        }

        // check to make sure we are working with mostly integers
        Matcher m = dateTimePattern.matcher(dicomDateTimeStr);
        if (!m.matches()) {
            throw new DataConversionException("malformed date time string: " + dicomDateTimeStr);
        }

        // first get the timezone if any
        String datetime = dicomDateTimeStr;
        String timezone = "+0000";
        int idx = dicomDateTimeStr.indexOf('-');
        if (idx < 0) {
            idx = dicomDateTimeStr.indexOf('+');
        }
        if (idx > -1) {
            timezone = dicomDateTimeStr.substring(idx);
            datetime = dicomDateTimeStr.substring(0, idx);
        }
        if (timezone.length() == 3) {
            timezone = timezone + "00";
        } else if (timezone.length() == 5) {
            // this is okay
        } else {
            throw new DataConversionException("malformed date time string: " + dicomDateTimeStr);
        }

        // now parse the date. all are optional....
        // hour
        int pos = 0;
        int len = 4;
        String yyyy = datetime.substring(pos, pos + len);
        pos += len;

        // minute
        String MM = "01";
        len = 2;
        if (datetime.length() >= pos + len) {
            MM = datetime.substring(pos, pos + len);
        }
        pos += len;

        // second
        String dd = "01";
        if (datetime.length() >= pos + len) {
            dd = datetime.substring(pos, pos + len);
        }
        pos += len;

        String date = yyyy + MM + dd;

        String time = "000000.000";
        if (datetime.length() >= pos + len) {
            time = DICOM2Model.convertTimeString(datetime.substring(8));
        }
        return date + time + timezone;
    }

    public DICOM2Model(ModelMap map) {
        this.map = map;
        this.mDictionary = map.getModelDict();

        // this.patientIdList = new HashMap<String, Object>();
    }

    // the object returned is only the one that is requested
    /*
     * public Vector<Object>
     * dicomToModelObject(com.pixelmed.query.QueryTreeModel tree, Class
     * targetClass) throws InstantiationException, IllegalAccessException,
     * DataConversionException { // plan: want to be able to return an arbitrary
     * model hierarchy level // so first thing - look at the current model and
     * identify at the current class, the deepest corresponding // dicom level.
     * 
     * this.patientIdList.clear();
     * 
     * // then find the deepest dicom level based on the attribute names. this
     * is the depth that needs to be traversed InformationEntity targetIE=null;
     * try { targetIE = map.getInformationEntityFromModelClass(targetClass); }
     * catch (ModelMapException e1) { // TODO Auto-generated catch block
     * e1.printStackTrace(); }
     * 
     * Class startClass = mDictionary.getModelBaseClass(); InformationEntity
     * startIE=null; try { startIE =
     * map.getInformationEntityFromModelClass(startClass); } catch
     * (ModelMapException e1) { // TODO Auto-generated catch block
     * e1.printStackTrace(); }
     * 
     * QueryTreeRecord treeRoot = QueryTreeRecord.class.cast(tree.getRoot());
     * 
     * // create the result set Vector<Object> result = new Vector<Object>();
     * 
     * System.out.println("called dicomToModelObject");
     * 
     * // call the parse function AttributeList al = new AttributeList();
     * this.patientIdList.clear(); for (int i = 0; i < treeRoot.getChildCount();
     * i++) { try {
     * createModelTree(QueryTreeRecord.class.cast(treeRoot.getChildAt(i)), al,
     * startIE, startClass, targetIE, targetClass, result); } catch
     * (ModelMapException e) { e.printStackTrace(); } }
     * 
     * return result; }
     */

    // parses the tree record that are the children of the supplied one.
    // if no matching ie or no children, then we return the empty vector.
    // we'll just walk through the tree looking for attributes that match the
    // requested object
    // Patient - mapped to study, with a separate key indicating patient
    // study - study + patient
    // series - series + study + patient
    // image - image + series + study + patient

    // starts at root concatenate attributelist as we move down.
    // return objects of type modelClass, while targetObjects is of type
    // targetClass
    // assumes a modelClass' children are at same or deeper IE
    // targetObjects holds the list of objects that has targetClass type
    // modelClass is the current class being created
    // targetIE is the deepest that the tree traversal should go
    // cumulativeAL is the concatenation of all attributeLists to this point.
    /*
     * private Vector<Object> createModelTree(QueryTreeRecord node,
     * AttributeList cumulativeAL, InformationEntity currIE, Class currClass,
     * InformationEntity targetIE, Class targetClass, Vector<Object>
     * targetObjects) throws ModelMapException, DataConversionException { if
     * (debug) System.out.println(indent + "called with modelclass = " +
     * currClass + " targetClass = " + targetClass + " node = " + node +
     * " targetie = " + targetIE);
     * 
     * indent += "  ";
     * 
     * 
     * if (node == null) { System.err.println(" modelClass " + currClass +
     * " node is null"); indent = indent.substring(0, indent.length()-2);
     * 
     * return null; }
     * 
     * // get al the attributes and accumulate, including the current node's
     * attributes AttributeList al =
     * node.getAllAttributesReturnedInIdentifier(); al.putAll(cumulativeAL);
     * 
     * InformationEntity target = targetIE; if
     * (target.equals(InformationEntity.PATIENT)) { target =
     * InformationEntity.STUDY; } // first check the node's ie to see if we
     * should parse this. InformationEntity ie = node.getInformationEntity();
     * InformationEntity modelIE =
     * map.getInformationEntityFromModelClass(currClass); InformationEntity
     * model = modelIE; if (model.equals(InformationEntity.PATIENT)) { model =
     * InformationEntity.STUDY; }
     * 
     * if (modelIE.compareTo(targetIE) > 0) { // the model that is being
     * processed is deeper than the target model's ie // so stop here. indent =
     * indent.substring(0, indent.length()-2); return null; } if (debug)
     * System.out.println(indent + "-curr IE = " + ie + " target IE = " +
     * targetIE + " model IE = " + modelIE + " model class = " +
     * currClass.getCanonicalName());
     * 
     * 
     * 
     * if (ie.compareTo(target) > 0) { // once the depth is determined, walk
     * through the tree until that depth // deeper than we need to be. by
     * definition there are no additional attributes we need. stop
     * 
     * // should not be here. if (debug2) System.out.println(indent +
     * "current IE is deeper than target: curr= " + ie + ", target= " + target +
     * " real target = " + targetIE); if (debug2) System.out.println(indent +
     * "model class is " + currClass.getCanonicalName()); indent =
     * indent.substring(0, indent.length()-2); return null; } // else // current
     * node depth is less than or equal to targetIE, so we can create objects
     * here
     * 
     * Vector<Object> result = new Vector<Object>();
     * 
     * // assume relationship is A contains B and C if (ie.equals(model)) { //
     * ie is same as the current class we are working with, so create objects //
     * including its children at the same level. also set the parent on the
     * children
     * 
     * // first create the object at this level corresponding to this node
     * (create A) Object obj = null; try { obj = currClass.newInstance(); if
     * (debug) System.out.println(indent + "-created class " + currClass +
     * " obj " + obj); } catch (InstantiationException e) { e.printStackTrace();
     * } catch (IllegalAccessException e) { e.printStackTrace(); }
     * 
     * boolean reuse = false; // multiple classes may have information entity
     * level of patient Class canonicalQueryRetrieveClass =
     * this.map.getModelClassFromQueryRetrieveLevel
     * (this.map.getRetrieveLevel(modelIE)); if
     * (modelIE.equals(InformationEntity.PATIENT) &&
     * currClass.getCanonicalName()
     * .equals(canonicalQueryRetrieveClass.getCanonicalName())) { // the model
     * class is at the PATIENT level. need to extract from study level
     * 
     * // if patient level, then we need to track what has already been created
     * Attribute patientId = al.get(com.pixelmed.dicom.TagFromName.PatientID);
     * if (patientId == null) { // if there is no patientId, and we are at study
     * level, then // can't construct patient object. return null; indent =
     * indent.substring(0, indent.length()-2); return null; } String pID =
     * patientId.getSingleStringValueOrEmptyString(); if
     * (this.patientIdList.containsKey(pID)) { // already created. obj =
     * this.patientIdList.get(pID); reuse = true; } else {
     * this.patientIdList.put(pID, obj); } }
     * 
     * 
     * if (obj == null) { indent = indent.substring(0, indent.length()-2);
     * return null; }
     * 
     * if (!reuse) { // populate the attributes. remember we have to do this for
     * all levels obj = populateFields(obj, al);
     * 
     * // for each, check the ie level, if current, create here // else, call
     * createModelTree with new class and current node. if (obj != null) {
     * result.add(obj); if (debug2) System.out.println("  added " +
     * obj.getClass().getCanonicalName() + " to " + result); } else { if (debug)
     * System.out.println(indent + "-object creation failed for " + currClass);
     * } if
     * (currClass.getCanonicalName().equals(targetClass.getCanonicalName())) {
     * targetObjects.add(obj); if (debug) System.out.println(indent +
     * "-QUEUE  added " + obj.getClass().getCanonicalName() + " to " +
     * targetObjects); } }
     * 
     * // then get all the children fields HashSet<Field> childFields =
     * (HashSet<Field>) mDictionary.getChildFieldsFromClass(currClass); if
     * (debug3) System.out.println(" *** currClass = " + currClass +
     * " childFields = " + childFields); Class parentClass =
     * mDictionary.getParentClassFromInformationEntityClass(currClass); if
     * (debug3) System.out.println(" *** currClass = " + currClass +
     * " parent class = " + parentClass); // walk through all possible children
     * (B and C fields) Vector<Object> children = null; Iterator iter =
     * childFields.iterator();
     * 
     * while (iter.hasNext()) { Field childField =
     * Field.class.cast(iter.next()); Class childClass = childField.getType();
     * if (childField.getType().isArray()) { childClass =
     * childClass.getComponentType(); }
     * 
     * // check to see if this child field is actually a parent to the obj,
     * since we have bidirectional links. // if so, then go on to the next
     * field. (if one of the child of B|C is A, then go on to next) // if
     * (debug) System.out.println("parentClass = " + parentClass +
     * " curr class " + modelClass + " childClass " + childClass); if
     * (parentClass != null &&
     * childClass.getCanonicalName().equals(parentClass.getCanonicalName())) {
     * if (debug2) System.out.println("SKIPPING parent " + parentClass +
     * " , child " + childClass); continue; }
     * 
     * // create the child objects. start at the same node, in case child class
     * has the same depth // let the function recurse down to the appropriate
     * place in the tree if (debug2)
     * System.out.println(" !creating model tree from " + node + " with target "
     * + targetIE + " childclass " + childClass + " target class " + targetClass
     * + " target Objects " + targetObjects); children = createModelTree(node,
     * cumulativeAL, this.map.getInformationEntityFromModelClass(childClass),
     * childClass, targetIE, targetClass, targetObjects); if (children == null
     * || children.size() <= 0) { // if (debug) System.out.println(indent +
     * "-children not created."); continue; }
     * 
     * 
     * // setting the children on a parent causes stack overflow because of the
     * bidirectional links. // but we also need to have linking for things like
     * instanceUID and CTPhysics // so restrict setting parent child
     * relationships to: // 1. set the child's parent always // 2. set the
     * children only if the child has no parent link
     * 
     * 
     * // now set the parent on the child (set A on B|C) // get the parent
     * setter HashSet<Field> parentFields = (HashSet<Field>)
     * mDictionary.getChildFieldsFromClass(childClass); Iterator<Field> iter2 =
     * parentFields.iterator(); Object[] args = new Object[1]; args[0] = obj;
     * boolean childHasLinkToParent = false; while(iter2.hasNext()) { Field
     * parentField = iter2.next(); Class parentClass2 = parentField.getType();
     * // if (debug2) System.out.println("parent field = " +
     * parentField.toString());
     * 
     * if (parentField.getType().isArray()) { // if (debug2)
     * System.err.println("ERROR: child cannot have more than 1 parent");
     * continue; }
     * 
     * if
     * (!parentClass2.getCanonicalName().equals(currClass.getCanonicalName())) {
     * if (debug2)
     * System.out.println("the field is not a parent for B|C.  so continue");
     * continue; }
     * 
     * childHasLinkToParent = true;
     * 
     * // match A on B|C, so set the value now Method parentSetter = null; try {
     * parentSetter =
     * mDictionary.getSettersFromField(parentField).firstElement(); } catch
     * (SecurityException e) { e.printStackTrace(); } catch
     * (NoSuchMethodException e) { e.printStackTrace(); }
     * 
     * // set the parent of B|C to A for (int i = 0; i < children.size(); i++) {
     * try { if (debug3) System.out.println(" ** parentSetter= " + parentSetter
     * ); if (debug3) System.out.println(" ** child element= " +
     * children.elementAt(i) ); if (debug3) System.out.println(" ** args= " +
     * args[0].getClass().getCanonicalName() );
     * parentSetter.invoke(children.elementAt(i), args); } catch
     * (IllegalArgumentException e) { if (debug3) System.out.println(indent +
     * " *parentSetter= " + parentSetter ); if (debug3)
     * System.out.println(indent + " *child element= " + children.elementAt(i)
     * ); if (debug3) System.out.println(indent + " *args= " +
     * args[0].getClass().getCanonicalName() ); e.printStackTrace(); } catch
     * (IllegalAccessException e) { if (debug3) System.out.println(indent +
     * " *parentSetter= " + parentSetter ); if (debug3)
     * System.out.println(indent + " *child element= " + children.elementAt(i)
     * ); if (debug3) System.out.println(indent + " *args= " +
     * args[0].getClass().getCanonicalName() ); e.printStackTrace(); } catch
     * (InvocationTargetException e) { if (debug3) System.out.println(indent +
     * " *parentSetter= " + parentSetter ); if (debug3)
     * System.out.println(indent + " *child element= " + children.elementAt(i)
     * ); if (debug3) System.out.println(indent + " *args= " +
     * args[0].getClass().getCanonicalName() ); e.printStackTrace(); } } }
     * 
     * if (!childHasLinkToParent) { // child does not have a parent link to me
     * Method childSetter = null; try { childSetter =
     * mDictionary.getSettersFromField(childField).firstElement(); } catch
     * (SecurityException e) { e.printStackTrace(); } catch
     * (NoSuchMethodException e) { e.printStackTrace(); } //if (debug)
     * System.out.println("children = " + children); args = new Object[1]; if
     * (childField.getType().isArray()) { args[0] =
     * castArray(children.toArray(), childField.getType().getComponentType()); }
     * else { if (debug3) System.out.flush(); if (debug3)
     * System.err.println("children element type = " +
     * children.firstElement().getClass().getCanonicalName() ); if (debug3)
     * System.err.println("child field type = " +
     * childField.getType().getCanonicalName() ); args[0] =
     * childField.getType().cast(children.firstElement()); }
     * 
     * // and set the child (set B|C on A) try { if (debug3)
     * System.out.println("  childSetter " + childSetter.toString()); if
     * (debug3) System.out.println("   object = " + obj + " args = " + args[0]);
     * childSetter.invoke(obj, args); } catch (IllegalArgumentException e) { if
     * (debug3) System.out.flush(); if (debug3)
     * System.err.println(" childSetter= " + childSetter ); if (debug3)
     * System.err.println(" obj = " + obj ); if (debug3)
     * System.err.println(" args= " + args[0].getClass().getCanonicalName() );
     * e.printStackTrace(); } catch (IllegalAccessException e) { if (debug3)
     * System.out.flush(); if (debug3) System.err.println(" childSetter= " +
     * childSetter ); if (debug3) System.err.println(" obj= " + obj ); if
     * (debug3) System.err.println(" args= " +
     * args[0].getClass().getCanonicalName() ); e.printStackTrace(); } catch
     * (InvocationTargetException e) { if (debug3) System.out.flush(); if
     * (debug3) System.err.println(" childSetter= " + childSetter ); if (debug3)
     * System.err.println(" obj= " + obj ); if (debug3)
     * System.err.println(" args= " + args[0].getClass().getCanonicalName() );
     * e.printStackTrace(); } } }
     * 
     * 
     * 
     * } else if (ie.compareTo(model) < 0 ){ // not at the right depth yet, go
     * deeper. AGGREGATE the children here, and pass them forward
     * 
     * // aggregate all the attributes including the current level
     * 
     * Vector<Object> temp = null; for (int i = 0; i < node.getChildCount();
     * i++) { if (debug2) System.out.println("  child count " +
     * node.getChildCount());
     * 
     * temp = createModelTree(QueryTreeRecord.class.cast(node.getChildAt(i)),
     * al, map.getInformationEntityFromModelClass(currClass), currClass,
     * targetIE,targetClass, targetObjects); if (temp != null) {
     * result.addAll(temp); } }
     * 
     * } // else overshot. stop this processing. e.g. child of series is study
     * and image. study should not be created again
     * 
     * // the above stuff creates the target objects, but we also need to get
     * the parent // traverse the tree again. not the most efficient
     * 
     * // if (debug2) System.out.println(" model = " + modelClass + " result = "
     * + result); indent = indent.substring(0, indent.length()-2); if (debug)
     * System.out.println(indent + "done with modelclass = " + currClass +
     * " targetClass = " + targetClass + " node = " + node + " targetie = " +
     * targetIE);
     * 
     * return result; }
     */

    public Object createObjectAndParentsFromAttributes(AttributeList al) throws ModelMapException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DataConversionException {
        AttributeTag tag = TagFromName.QueryRetrieveLevel;
        Attribute val = al.get(tag);
        if (val == null) throw new ModelMapException("attribute list does not contain query retrieve level"); 
        Class currClass = this.map.getModelClassFromQueryRetrieveLevel(val.getSingleStringValueOrNull());
        Object root = currClass.newInstance();
        Object curr = populateFields(root, al);
        
        Class parentClass = this.mDictionary.getParentClassFromInformationEntityClass(currClass);
        Object parent;
        boolean found = false;
        while (parentClass != null ) {
        	
        	parent = parentClass.newInstance();
        	parent = populateFields(parent, al);
        	
        	for (Method m : currClass.getMethods()) {
        		for (Class c : m.getParameterTypes()) {
        			if (c.getCanonicalName().equals(parentClass.getCanonicalName())) {
        				m.invoke(curr, parent);
        				found = true;
        				break;
        			}
        		}
        		if (found) break;
        	}
        	found = false;
        	
        	curr = parent;
        	currClass = parentClass;
         	parentClass = this.mDictionary.getParentClassFromInformationEntityClass(parentClass);
        }
        
        return root;
    }
    
    // provide a clean way to case an array from Object to the proper type
    /*
     * private Object castArray(Object objects, Class<?> type) { //if (debug)
     * System.out.println(objects); Object newArray = Array.newInstance(type,
     * Array.getLength(objects)); for (int i = 0; i < Array.getLength(objects);
     * i++) { if (type.isArray()) { Array.set(newArray, i,
     * castArray(Array.get(objects, i), type.getComponentType())); }
     * Array.set(newArray, i, type.cast(Array.get(objects,i))); } return
     * newArray; }
     */

    public Object populateFields(Object modelObj, AttributeList al) throws ModelMapException, DataConversionException {
        // walk through the attribute list and see which ones are in the model
        // attribute names list
        myLogger.debug("Entering populateFields");
        Iterator<AttributeTag> iter = al.keySet().iterator();

        while (iter.hasNext()) {
            AttributeTag tag = (AttributeTag) iter.next();
            // get the attribute value
            Attribute attr = al.get(tag);

            // what are the matching attributenames? could be more than one,
            // e.g. x, y z of patient position
            HashMap<String, Integer> fieldNames = map.getModelAttributeNamesFromAttributeTag(tag);
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("Tag name:" + map.getDicomDict().getNameFromTag(tag));
            }

            // debug
            if (fieldNames == null) {
                String tagName = this.map.getDicomDict().getNameFromTag(tag);
                if (tagName == null || tagName.equals("")) {
                    tagName = " no matching name in dictionary ";
                }
                if (production)
                    myLogger.warn("There are no attributes for tag = " + tagName);
                continue;
            }

            // walk through the attribute Names
            Iterator<String> iter2 = fieldNames.keySet().iterator();
            while (iter2.hasNext()) {
                // first get the name
                String fieldName = iter2.next();
                int attrIndex = fieldNames.get(fieldName);

                // check to see if field belongs to the same class as the
                // modelObj.
                Class<?> fieldParentClass = mDictionary.getInformationEntityClassFromAttributeName(fieldName);
                if (!fieldParentClass.isInstance(modelObj)) {
                    continue;
                }

                // then walk through the setters, and set the values.
                // assume the setter method is of the form
                // "void setXXX(Type xxx)"
                // get the first setter
                Method m = null;
                try {
                    if (myLogger.isDebugEnabled()) {
                        String msg = "field name = " + fieldName + "; DICOM2Model : "
                                + mDictionary.getSettersFromAttributeName(fieldName);
                        myLogger.debug(msg);
                    }
                    m = mDictionary.getSettersFromAttributeName(fieldName).get(0);
                } catch (Exception e) {
                    String msg = "Error getting setter method for field: " + fieldName;
                    myLogger.error(msg, e);
                    throw new ModelMapException(msg, e);
                }
                if (m == null) {
                    throw new ModelMapException("ERROR: no such method for " + fieldName);
                }
                // java Method.isAccessible()
                if (!Modifier.isPublic(m.getModifiers())) {
                    throw new ModelMapException("ERROR: method for field " + fieldName + " is not public " + m);
                }

                // paramClasses.length should be 1, as hard coded in
                // ModelDictionary
                if (m.getParameterTypes().length > 1) {
                    throw new ModelMapException("ERROR: first setter in list should have a single param");
                }
                Class<?> paramClass = m.getParameterTypes()[0];
                Object[] params = new Object[1];
                if (myLogger.isDebugEnabled()) {
                    myLogger.debug("param class = " + paramClass);
                }
                // now convert the parameters.
                params[0] = this.convertAttributeToObject(attr, paramClass, attrIndex);
                if (myLogger.isDebugEnabled()) {
                    myLogger.debug("param 0 = " + params[0]);
                }
                // then invoke the setter
                if (params[0] != null) {
                    try {
                        m.invoke(modelObj, params);
                    } catch (Exception e) {
                        String msg = "method: " + m + ";  modelObj: " + modelObj + ";  params: "
                                + params[0].getClass().getCanonicalName();
                        myLogger.warn(msg, e);
                    }
                }
            } // loop over fieldnames for the tag
        } // loop over tags
        myLogger.debug("Exiting populateFields");

        return modelObj;
    }

    private Object convertAttributeToObject(Attribute attr, Class<?> paramClass, int attrIndex)
            throws DataConversionException {

        byte[] vr = attr.getVR();

        // first get the values. getStringValues appears to be a good generic
        // choice.
        // we would not need to know the VR representation this way, and
        // therefore the details of the mapping...
        String[] strValues = null;
        try {
            strValues = attr.getStringValues();
        } catch (DicomException e) {
            String msg = "Error getting values of attributes: " + Arrays.toString(strValues);
            myLogger.warn(msg, e);
        }

        if (strValues == null) {
            return null;
        }

        // now let the paramClass parse the values on contructor.
        if (paramClass.isArray()) {
            // get the underlying type

            Class<?> componentType = paramClass.getComponentType();
            Object[] values = new Object[strValues.length];
            // create the array
            for (int i = 0; i < strValues.length; i++) {
                values[i] = this.createObjectFromAttributeValue(componentType, strValues[i], vr);
            }

            return values;

        } else {
            int idx = attrIndex;
            if (attrIndex < 0) {
                idx = 0;
            }
            if (myLogger.isDebugEnabled()) {
                myLogger.debug(" idx = " + idx + " value = " + strValues[idx] + " vr = "
                        + ValueRepresentation.getAsString(vr));
            }
            // not an array, so don't need to loop
            return createObjectFromAttributeValue(paramClass, strValues[idx], vr);
        }
    }

    private Class<?> getWrapperClass(Class<?> inputClass) {
        if (!inputClass.isPrimitive()) {
            return inputClass;
        }

        String className = inputClass.getCanonicalName();
        if (className.equals("char"))
            className = "character";
        if (className.equals("void"))
            return null;
        String wrapperName = "java.lang." + className.substring(0, 1).toUpperCase() + className.substring(1);
        try {
            return Class.forName(wrapperName);
        } catch (ClassNotFoundException e) {
            myLogger.error("Error in getWrapperClass", e);
            return null;
        }
    }

    /**
     * @param paramClass
     * @param strValues
     * @param constructorParams
     * @param construct
     * @param idx
     * @return
     * @throws DataConversionException
     */
    private Object createObjectFromAttributeValue(Class<?> paramClass, String strValue, byte[] vr)
            throws DataConversionException {
        if (strValue == null) {
            return null;
        }
        if (paramClass.isPrimitive()) {
            if (myLogger.isDebugEnabled())
                myLogger.debug("primitive type " + paramClass + " for value " + strValue);
        }
        Class<?> paramType = this.getWrapperClass(paramClass);

        // TCP: using isAssignableFrom to check to see if paramType is a
        // subclass or same class as the target class.
//        if (java.util.Date.class.isAssignableFrom(paramType)) {
//            if (debug4)
//                System.out.println("paramClass is " + paramType.getCanonicalName() + ", value is " + strValue);
//        } else if (java.util.Calendar.class.isAssignableFrom(paramType)) {
//            if (debug4)
//                System.out.println("paramClass is " + paramType.getCanonicalName() + ", value is " + strValue);
//        } else if (org.apache.axis.types.Time.class.isAssignableFrom(paramType)) {
//            if (debug4)
//                System.out.println("paramClass is " + paramType.getCanonicalName() + ", value is " + strValue);
//        } else if (paramType.getPackage().getName().equals("java.lang")) {
//            if (debug4)
//                System.out.println("paramClass is " + paramType.getCanonicalName() + ", value is " + strValue);
//        } else {
//            System.err.println("ERROR: paramClass is probably not deserializable " + paramType.getCanonicalName()
//                    + ", value is " + strValue);
//        }

        Object value = null;
        try {
            if (java.util.Calendar.class.isAssignableFrom(paramType)) {
                Calendar temp = DICOM2Model.dateTimeFormatter.getCalendar();
                temp.setTime(DICOM2Model.dateTimeFormatter.parse(DICOM2Model.convertDateTimeString(strValue)));
                value = temp;
            } else {
                Class<?>[] params = { strValue.getClass() };
                Constructor<?> construct = null;
                try {
                    construct = paramType.getConstructor(params);
                } catch (Exception e) {
                    myLogger.error("Error getting constructor");
                    throw e;
                }

                // ASHISH//
                if (ValueRepresentation.isDateVR(vr)) {
                    // return a java Date object
                    value = DICOM2Model.dateFormatter.parse(DICOM2Model.convertDateString(strValue));
                } else if (ValueRepresentation.isTimeVR(vr)) {
                    // return a Time object (axis)
                    value = construct.newInstance(new Object[] { DICOM2Model.convertTimeString(strValue) });
                } else if (ValueRepresentation.isDateTimeVR(vr)) {
                    // return an object, which presumably is a java Date object.
                    value = DICOM2Model.dateTimeFormatter.parseObject(DICOM2Model.convertDateTimeString(strValue));
                } else {
                    value = construct.newInstance(new Object[] { strValue });
                }
            }
        } catch (Exception e) {
            myLogger.error("Error in createObjectFromAttributeValue", e);
        }
        return value;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        String test = "2007.01.31";
        try {
            System.out.println(convertDateString(test));
        } catch (DataConversionException e5) {
            // TODO Auto-generated catch block
            e5.printStackTrace();
        }

        // test object creation
        ModelMap map = null;
        try {
            map = new ModelMap();
        } catch (FileNotFoundException e4) {
            // TODO Auto-generated catch block
            e4.printStackTrace();
        } catch (ModelMapException e4) {
            // TODO Auto-generated catch block
            e4.printStackTrace();
        } catch (IOException e4) {
            // TODO Auto-generated catch block
            e4.printStackTrace();
        } catch (ClassNotFoundException e4) {
            // TODO Auto-generated catch block
            e4.printStackTrace();
        }
        DICOM2Model d2m = new DICOM2Model(map);
        // d2m.debug = true;

        AttributeList al = new AttributeList();

        DicomDictionary dict = new DicomDictionary();

        AttributeTag tag = com.pixelmed.dicom.TagFromName.InstanceNumber;
        byte[] vr = dict.getValueRepresentationFromTag(tag);
        Attribute attr = null;
        try {
            attr = AttributeFactory.newAttribute(tag, vr);
        } catch (DicomException e) {
            e.printStackTrace();
        }
        if (attr != null) {
            try {
                attr.addValue("12345");
            } catch (DicomException e) {
                e.printStackTrace();
            }
        }
        al.put(tag, attr);

        Object out = null;
        try {
            out = d2m.convertAttributeToObject(attr, String.class, -1);
        } catch (DataConversionException e5) {
            // TODO Auto-generated catch block
            e5.printStackTrace();
        }
        System.out.println(out.toString());

        // test getting one element from an array
        tag = com.pixelmed.dicom.TagFromName.PixelSpacing;
        vr = dict.getValueRepresentationFromTag(tag);
        attr = null;
        try {
            attr = AttributeFactory.newAttribute(tag, vr);
        } catch (DicomException e) {
            e.printStackTrace();
        }
        if (attr != null) {
            try {
                attr.addValue(10.5);
                attr.addValue(20.7);
            } catch (DicomException e) {
                e.printStackTrace();
            }
        }
        al.put(tag, attr);

        // try setting the values
        Class instanceClass = null;
        try {
            instanceClass = map.getModelClassFromQueryRetrieveLevel(map.getRetrieveLevel(InformationEntity.INSTANCE));
        } catch (ModelMapException e4) {
            e4.printStackTrace();
        }
        Object image = null;
        try {
            image = instanceClass.newInstance();
        } catch (InstantiationException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        } catch (IllegalAccessException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
        try {
            d2m.populateFields(image, al);
        } catch (ModelMapException e) {
            e.printStackTrace();
        } catch (DataConversionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HashMap<String, Integer> names = null;
        try {
            names = map.getModelAttributeNamesFromAttributeTag(tag);
        } catch (ModelMapException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
        if (names != null) {

            System.out.println("TODO:  create test to see if we can get values out of the object");

        }

    }

}
