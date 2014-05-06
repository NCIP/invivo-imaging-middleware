/**
 * 
 */
package gov.nih.nci.ivi.dicom.modelmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tpan
 * This is an abstract data dictionary that is loaded from the file system.
 * This allows easier swap of different models for the caGrid data structure.
 * 
 * there is 2 basic restrictions for now, which are that cycles in the information model
 * are not represented, and a class cannot have multiple parents.
 * 
 * The model is initialized from a class object.  The other class in the information model are
 * automatically traversed.  Some of the characteristics are 1. the links needs to be bidirectional if
 * the entire information model is to be traverse, otherwise only a part of the information model
 * be represented.  2. hierarchy information is tracked only if the user explicitly states that the
 * class is a root level class.  This information is not explicit in the model therefore needs to
 * be specified explicitly.
 * 
 * The attribute name mapping and class mapping are not affected by information hierachy root or nonroot,
 * if the associations are bidirectional.
 * 
 * works with attributes right now, and no explicit support or problems with arrays
 * 
 */

// TODO: switching to xml based hierarchy might be better. Currently, there is no good way to check the validity of the hierarchy properties file.
// TODO: need regression testing of the actual content of the dictionary
public class ModelDictionary {
	private static final String ROOT_CLASS_IDENTIFIER = "root";
	private static final String PROPERTIES_SEPARATOR = ",";
	// ideally, use singleton pattern.  but this appears to be difficult with abstract class
	// because different constructors are needed
	
	private HashMap<String, Class> nameToClassMap = new HashMap<String, Class>();
	private HashMap<String, Field> attributeNameToFieldMap = new HashMap<String, Field>();
	private HashMap<String, HashSet<String>> informationEntityNameToAttributeNamesMap = new HashMap<String, HashSet<String>>();

	// was informationEntityParentMap
	private HashMap<Class, Class> childToParentMap = new HashMap<Class, Class>();
	
	// was informationEntityChildrenMap
	private HashMap<Class, HashSet<Field>> parentToChildrenMap = new HashMap<Class, HashSet<Field>>();
	
	private Class modelBaseClass;
	private boolean parseHierarchy;
	private boolean loadFromFile;
	private Properties hierarchy;
	private int debug = 0;
	
	// parseHierarchy instructs the model dictionary to parse the class hierarchy from the toplevel class.
	// hierarchyFilename specifies the file from which the hierarchy information is to be loaded.
	// these 2 are mutually exclusive, with hierarchy file taking precedence.
	// if both are false and null (or empty), then no hierarchy information is generated.
	public ModelDictionary() throws ModelMapException, IOException {
		ClassLoader cl = ModelDictionary.class.getClassLoader();
		InputStream is = cl.getResourceAsStream("resources/modelmap/NCIAModelHierarchy.properties");
		
		loadDictionaryFromStream(is);
	}
	public ModelDictionary(boolean parseHierarchy) throws ModelMapException {
		
		this.parseHierarchy = parseHierarchy;
		this.loadFromFile = false;
	}
	// allow only single root
	public ModelDictionary(File hierarchyFile) throws ModelMapException, FileNotFoundException, IOException {
		if (hierarchyFile == null) {
			throw new ModelMapException("Hierarchy File to be parsed cannot be null ");
		}
		if (!hierarchyFile.exists()) {
			throw new ModelMapException("Hierarchy File to be parsed does not exist " + hierarchyFile);
		}
		if (!hierarchyFile.canRead()) {
			throw new ModelMapException("Hierarchy File to be parsed is not readable " + hierarchyFile);
		}
		
		this.loadDictionaryFromStream(new FileInputStream(hierarchyFile));
	}
		
	private void loadDictionaryFromStream(InputStream is) throws IOException, ModelMapException {
		this.parseHierarchy = false;
		this.loadFromFile=true;

		// check the file for validity
		this.hierarchy = new Properties();
		hierarchy.load(is);

		this.checkHierarchy();
	}
		
	private void checkHierarchy() throws ModelMapException, IOException {
		if (!hierarchy.containsKey(ROOT_CLASS_IDENTIFIER)) {
			throw new ModelMapException("Hierarchy does not have a root element");
		}
		
		Iterator iter = hierarchy.values().iterator();
		while (iter.hasNext()) {
			String childStr = String.class.cast(iter.next());
			
			if ((childStr != null) && !childStr.equals("")) {
				String[] tokens = childStr.split(PROPERTIES_SEPARATOR);
				for (int i = 0; i < tokens.length; i++) {
					// check for completeness of the hierarchy specification
					if (!hierarchy.containsKey(tokens[i].trim())) {
						throw new ModelMapException("Missing entry " + tokens[i].trim() + " in hierarchy " + hierarchy.toString());
					}
				}
			}
		}		
	}

	public void initialize(String targetClassName) throws FileNotFoundException, IOException, ModelMapException, ClassNotFoundException {
		this.initialize(Class.forName(targetClassName));
	}	

	
	public void initialize(Class targetClass) throws FileNotFoundException, ModelMapException, IOException {
		if (targetClass == null) {
			throw new ModelMapException("Target Class cannot be null ");
		}
		this.modelBaseClass = targetClass;

		
		if (this.loadFromFile) {
			HashSet<Class> classes = null;
			try {
				classes = this.createHierarchy();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			if (classes == null) {
				throw new ModelMapException("Parse failed for Hierarchy File.  no classes identified " + hierarchy.toString());
			}
					
			if (!classes.contains(this.modelBaseClass)) {
				throw new ModelMapException("Unable to locate the target class " + this.modelBaseClass.getCanonicalName() + " in the classes list ");
			}
		}		
				
		this.handleComplexAttribute(this.modelBaseClass, null);
	}
	
	// allows multiple roots.  really only for academic completeness.
	private HashSet<Class> createHierarchy() throws ClassNotFoundException, FileNotFoundException, IOException {

				
		Iterator iter = hierarchy.keySet().iterator();
		HashSet<Class> classes = new HashSet<Class>();
		while (iter.hasNext()) {
			String classname = String.class.cast(iter.next());
			
			// create the parent class, if it's not root
			Class parent = null;
			if (!classname.equalsIgnoreCase(ROOT_CLASS_IDENTIFIER)) {
				parent = Class.forName(classname);
				//System.out.println(parent.getCanonicalName());
				classes.add(parent);
			}
			
			/** create the parent mapping. */
			// root will have 1 or more children, whose parent are null
			// leaf will have null children, so will not be added
			String childStr = String.class.cast(hierarchy.get(classname));
			Class[] children = null;
			if ((childStr != null) && !childStr.equals("")) {
				String[] tokens = childStr.split(PROPERTIES_SEPARATOR);
				children = new Class[tokens.length];
				for (int i = 0; i < tokens.length; i++) {
					
					children[i] = Class.forName(tokens[i].trim());
					//System.out.println("    " + children[i].getCanonicalName());

					if (!this.childToParentMap.containsKey(children[i])) {
						this.childToParentMap.put(children[i], parent);
					}
				}
			}
			
			/** create the children mapping */
			// root will have 1 or more children but no class itself, so ignored
			// leave will have 0 children, so empty hashset (childFields. but it's still added
			if (parent!=null) {
				HashSet<Field> childFields = new HashSet<Field>();
				this.parentToChildrenMap.put(parent, childFields);
			
				Field[] fields = parent.getDeclaredFields();
				java.util.Arrays.sort(fields, new Comparator<Field>() {
					public int compare(Field arg0, Field arg1) {
						Class type1 = arg0.getType();
						Class type2 = arg1.getType();
						if (type1.isArray()) {
							type1 = type1.getComponentType();
						}
						if (type2.isArray()) {
							type2 = type2.getComponentType();
						}
						return type1.getCanonicalName().compareTo(type2.getCanonicalName());
					}
				});
					
				if (children == null) {
					continue;
				}
				
				for (int j = 0; j < children.length; j++) {
					
					int index = java.util.Arrays.binarySearch(fields, children[j], new Comparator<Object>() {

						public int compare(Object o1, Object o2) {
							if (!Field.class.isInstance(o1)) {
								System.err.println("ERROR: o1 is not a Field: " + o1.getClass().getCanonicalName());
								return -1;
							}

							if (!Class.class.isInstance(o2)) {
								System.err.println("ERROR: o2 is not a Class: " + o2.getClass().getCanonicalName());
								return -2;
							}
							
							Field f = Field.class.cast(o1);
							Class fieldType = f.getType();
							if (fieldType.isArray()) {
								fieldType = fieldType.getComponentType();
							}
							Class c = Class.class.cast(o2);
							
							return fieldType.getCanonicalName().compareTo(c.getCanonicalName());
						}
						
					});
					
					if (index >= 0) {
						childFields.add(fields[index]);
					}
				}
			}
		}
		return classes;
	}
	
	private void handleSimpleAttribute(Field f) throws ModelMapException {
		if (f == null) {
			throw new ModelMapException("simple attribute field should not be null");
		}
		
		String className = f.getDeclaringClass().getCanonicalName();
		this.attributeNameToFieldMap.put(className +"." + f.getName(), f);
		
		HashSet<String> attributeNames = null;
		if (this.informationEntityNameToAttributeNamesMap.containsKey(className)) {
			attributeNames = this.informationEntityNameToAttributeNamesMap.get(className);
		} else {
			attributeNames = new HashSet<String>();
		}
		attributeNames.add(className +"." + f.getName());
		this.informationEntityNameToAttributeNamesMap.put(className, attributeNames);
	}

	// for axis generated classes, we can try to automatically derive this.
	// the assumptions are: start with fields of the thing, and if they are
	// axis type, java type, or primitive, we are probably dealing iwth attributes.
	private void handleComplexAttribute(Class currClass, Class container) throws ModelMapException {
		if (currClass == null) {
			throw new ModelMapException("target class to process should not be null");
		}
		
		// first check to see if class is already in map.
		// if so we are in a cycle and we should stop.
		if (this.nameToClassMap.containsKey(currClass.getCanonicalName())) {
			return;
		}
		
		this.nameToClassMap.put(currClass.getCanonicalName(), currClass);
		
		// populate the hierarchy only if the starting class is the informationModelRoot
		if (this.parseHierarchy && !this.loadFromFile) {		
			this.childToParentMap.put(currClass, container);
			if (debug >= 3) System.out.println("INFO: populating informationEntityParentMap " + currClass.getCanonicalName());
		}	

		// the informationEntityChildrenMap should be populated from file at this point.
		HashSet<Field> children = null;
		if (!this.parentToChildrenMap.containsKey(currClass)) {
			children = new HashSet<Field>();
			this.parentToChildrenMap.put(currClass, children);
			if (debug >= 3) System.out.println("INFO: populating informationEntityChildrenMap " + currClass.getCanonicalName());
		} else {
			//System.out.println("currClass = " + currClass);
			//System.out.println("    children = " + this.informationEntityChildrenMap.get(currClass));
			children = this.parentToChildrenMap.get(currClass);
		}
		
		// looking for attributes declared in this class
		Field[] attributes = currClass.getDeclaredFields();
		for (int i = 0; i < attributes.length; i++) {
			Field f = attributes[i];
			String name = f.getName();

			// exclude the following: typeDesc, __hashCodeCalc, __equalsCalc
			if (name.endsWith("typeDesc") ||
					name.endsWith("__hashCodeCalc") ||
					name.endsWith("__equalsCalc")) {
				continue;
			}
			
			// get the type, and if it is an array, get the actual type
			Class type = f.getType();
			if (type.isArray()) {
				type = type.getComponentType();
			}
			
			// if simple type or java type, then add to lists
			if (type.isPrimitive()) {
				this.handleSimpleAttribute(f);
			} else {
				Package pack = type.getPackage();					
				if (pack == null || pack.equals("")) {
					//no package.  assume complex type.  complex types need to be navigated...
					children.add(f);
					this.handleComplexAttribute(type, currClass);
				} else if (pack.getName().startsWith("java") ||
						pack.getName().startsWith("org.apache.axis.types")) {
					// assume java and axis types are all okay
					// TODO: later setup an accepted list of java types
					this.handleSimpleAttribute(f);
				} else {
					// if complex type, then need to be 
					children.add(f);
					this.handleComplexAttribute(type, currClass);
				}
			}
		}
	}


	// given a part of an attribute name, find the set of attributes that match this.
	// allows things like InstanceUID._value
	public Collection<String> getAttributeNameContains(String name) throws ModelMapException {
		if (name == null) {
			throw new ModelMapException("Attribute name parameter for getAttributeNameContains should not be null");
		}
		
		TreeSet<String> hs = new TreeSet<String>();
		
		Iterator<String> iter = this.attributeNameToFieldMap.keySet().iterator();
		while (iter.hasNext()) {
			String attrFullName = String.class.cast(iter.next());
			
			if (attrFullName.contains(name)) {
				hs.add(attrFullName);
			}
		}
		return hs;
	}
	public Collection<String> getAttributeNameMatches(String name) throws ModelMapException {
		if (name == null) {
			throw new ModelMapException("Attribute name parameter for getAttributeNameContains should not be null");
		}
		
		TreeSet<String> hs = new TreeSet<String>();
		Pattern p = Pattern.compile(".*\\." + name + "([^\\.])*\\._value");
		Iterator<String> iter = this.attributeNameToFieldMap.keySet().iterator();
		while (iter.hasNext()) {
			String attrFullName = String.class.cast(iter.next());
			
			if (attrFullName.endsWith("." + name) || 
					p.matcher(attrFullName).matches()) {
				hs.add(attrFullName);
			}
		}
		return hs;
	}
	
	// attribute name is fully qualified if the attribute is of the form "xxx.attrname"
	public boolean isNameValid(String name) throws ModelMapException {
		if (name == null || name.equals("")) {
			throw new ModelMapException("ERROR: attribute is null");
		}
/*		int index = attribute.lastIndexOf('.');
		if (index < 0) {
			return false;
		}
		String className = attribute.substring(0,index);
		if (className == null || className.equals("")) {
			return false;
		}
*/
		if(nameToClassMap.containsKey(name))
			return true;
		if(attributeNameToFieldMap.containsKey(name))
			return true;
		return false;
	}
	
	public HashSet<String> getAttributeNamesFromInformationEntityClass(Class ie) throws ModelMapException {
		if (ie == null) {
			throw new ModelMapException("ERROR: information entity class is null");
		}
		
		return this.informationEntityNameToAttributeNamesMap.get(ie.getCanonicalName());
	}
	
	// equivalent to dicom dict getInformationEntityFromTag
	// attribute name should be fully qualified.
	public Class getInformationEntityClassFromAttributeName(String attribute) throws ModelMapException {
		if (attribute == null || attribute.equals("")) {
			throw new ModelMapException("attribute parameter should not be null or empty");
		}
		if (!isNameValid(attribute)) {
			throw new ModelMapException("attribute " + attribute + " needs to be fully qualified");
		}

		Field f = this.attributeNameToFieldMap.get(attribute);
		if (f == null) {
			return null;
		} else {
			return f.getDeclaringClass();
		}
	}
	
	public Class getParentClassFromInformationEntityClass(Class ie) throws ModelMapException {
		if (ie == null) {
			throw new ModelMapException("class parameter should not be null");
		}
		return this.childToParentMap.get(ie);
	}
	
	public Vector<Class> getLineage(Class ie) throws ModelMapException {
		if (ie == null) {
			throw new ModelMapException("the information entity class is null");
		}
		
		Vector<Class> result = new Vector<Class>();
		if (!this.childToParentMap.containsKey(ie)) {
			return result;
		}
		result.add(ie);
		
		Class parent = this.childToParentMap.get(ie);
		while (parent != null) {
			result.add(parent);
			parent = this.childToParentMap.get(parent);
		}
		return result;
	}
	
	// equivalent to dicom dict getValueRepresentationFromTag 
	// attribute name should be fully qualified.
	public Class getAttributeClassFromAttributeName(String attribute) throws ModelMapException {
		if (attribute == null) {
			throw new ModelMapException("attribute should not be null");
		}
		if (!isNameValid(attribute)) {
			throw new ModelMapException("attribute " + attribute + " needs to be fully qualified");
		}
		Field f = this.attributeNameToFieldMap.get(attribute);
		if (f == null) {
			return null;
		} else {
			return f.getType();
		}
	}
	
	// equivalent to dicom dict getTagIterator
	// attribute name should be fully qualified.
	public Iterator<Field> getAttributeIterator() {
		return this.attributeNameToFieldMap.values().iterator();
	}
	public Iterator<String> getInformationEntityNameIterator() {
		return this.nameToClassMap.keySet().iterator();
	}
	
	public Collection<Field> getChildFieldsFromClass(Class me) throws ModelMapException {
		if (me == null) {
			throw new ModelMapException("class parameter should not be null");
		}
		return this.parentToChildrenMap.get(me);
	}
		
	
	// equivalent to dicom dict getTagFromName
	// attribute name should be fully qualified.
	public Vector<Method> getSettersFromAttributeName(String attribute) throws ModelMapException, SecurityException, NoSuchMethodException  {
		if (attribute == null) {
			throw new ModelMapException("attribute should not be null");
		}
		if (!isNameValid(attribute)) {
			throw new ModelMapException("attribute " + attribute + " needs to be fully qualified");
		}
		Field f = this.attributeNameToFieldMap.get(attribute);
		
		return getSettersFromField(f);
	}
	// first method are never indexed (whole array).  HARDCODED support for only set array or set individual element
	public Vector<Method> getSettersFromField(Field f) throws SecurityException, NoSuchMethodException, ModelMapException {
		if (f == null) {
			throw new ModelMapException("field should not be null");
		}
		Vector<Method> result = new Vector<Method>();
		if (!this.attributeNameToFieldMap.containsValue(f)) {
			// if this field is actually a child field
			Class type = f.getType();
			if (type.isArray()) {
				type = type.getComponentType();
			}
			
			if (!this.childToParentMap.containsKey(type)) {
				throw new ModelMapException("field " + f.getName() + " is not part of this model dictionary.");
			}
			
			// this is a child field, so return empty
		}
		
		Class container = f.getDeclaringClass();
		String name = f.getName();
		String targetMethodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1);
		
		Method ms;
		ms = container.getMethod(targetMethodName, new Class[] { f.getType() } );
		result.add(ms);
		if (f.getType().isArray()) {
			ms = container.getMethod(targetMethodName, new Class[] { int.class, f.getType().getComponentType() });
			result.add(ms);
		}
		
		return result;
	}
	
	public Vector<Method> getGettersFromAttributeName(String attribute) throws ModelMapException, SecurityException, NoSuchMethodException {
		if (attribute == null) {
			throw new ModelMapException("attribute should not be null");
		}
		if (!isNameValid(attribute)) {
			throw new ModelMapException("attribute " + attribute + " needs to be fully qualified");
		}
		Field f = this.attributeNameToFieldMap.get(attribute);
		
		return this.getGettersFromField(f); 
	}
	// first method are never indexed (whole array).  HARDCODED support for only get array or get individual element
	public Vector<Method> getGettersFromField(Field f) throws SecurityException, NoSuchMethodException, ModelMapException {
		if (f == null) {
			throw new ModelMapException("field should not be null");
		}

		
		Vector<Method> result = new Vector<Method>();
		if (!this.attributeNameToFieldMap.containsValue(f)) {
			// if this field is actually a child field
			Class type = f.getType();
			if (type.isArray()) {
				type = type.getComponentType();
			}
				
			if (!this.childToParentMap.containsKey(type)) {
				throw new ModelMapException("field " + f.getName() + " is not part of this model dictionary.");
			}
			
			// this is a child field, so return empty
		}
	
		
		Class container = f.getDeclaringClass();
		String name = f.getName();
		String targetMethodName = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
		
		Method ms;
		ms = container.getMethod(targetMethodName, new Class[] { } );
		result.add(ms);
		if (f.getType().isArray()) {
			ms = container.getMethod(targetMethodName, new Class[] { int.class });
			result.add(ms);
		}
		
		return result;
	}
	
	public Vector<Method> getIsFromAttributeName(String attribute) throws ModelMapException, SecurityException, NoSuchMethodException {
		if (attribute == null) {
			throw new ModelMapException("attribute should not be null");
		}
		if (!isNameValid(attribute)) {
			throw new ModelMapException("attribute " + attribute + " needs to be fully qualified");
		}
		Field f = this.attributeNameToFieldMap.get(attribute);
		
		return getIsFromField(f); 
	}
	
	public Vector<Method> getIsFromField(Field f) throws SecurityException, NoSuchMethodException, ModelMapException {
		if (f == null) {
			throw new ModelMapException("attribute should not be null");
		}
		if (!this.attributeNameToFieldMap.containsValue(f)) {
			throw new ModelMapException("field is not part of this model dictionary.");
		}

		Vector<Method> result = new Vector<Method>();
		
		Class container = f.getDeclaringClass();
		String name = f.getName();
		String targetMethodName = "is" + name.substring(0,1).toUpperCase() + name.substring(1);
		
		Method ms;
		ms = container.getMethod(targetMethodName, new Class[] { } );
		result.add(ms);
		
		return result;
	}
	
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("InformationEntityNameToClassMap : \n" + this.nameToClassMap.toString().replace(",", "\n") + "\n\n");
		buf.append("InformationEntityParentMap : \n" + this.childToParentMap.toString().replace(",", "\n") + "\n\n");
		buf.append("InformationEntityChildrenMap : \n" + this.parentToChildrenMap.toString().replace(",", "\n") + "\n\n");
		buf.append("AttributeNameToFieldMap : \n" + this.attributeNameToFieldMap.toString().replace(",", "\n") + "\n\n");
		buf.append("InformationEntityNameToAttributeNAmesMap : \n" + this.informationEntityNameToAttributeNamesMap.toString().replace(",","\n") + "\n\n");		
		
		buf.append("Attribute Field and Accessor methods: \n");
		Iterator<Field> attributeFieldIter = this.getAttributeIterator();
		Collection<String> col = null;
		Field f = null;
		Iterator<String> attributeNameIter = null;
		String fullname = null;
		while (attributeFieldIter.hasNext()) {
			f = attributeFieldIter.next();
			
			col = null;
			try {
				if (f.getName().equals("_value")) {
					String classname = f.getDeclaringClass().getSimpleName();
					col = this.getAttributeNameMatches(classname.substring(0, classname.lastIndexOf("Type")));
				} else {
					col = this.getAttributeNameMatches(f.getName());
				}
			} catch (ModelMapException e) {
				e.printStackTrace();
			}
			
			attributeNameIter = col.iterator();
			fullname = null;
			while (attributeNameIter.hasNext()) {
				fullname = attributeNameIter.next();
				buf.append("names = " + fullname + "\n");
			
				if (fullname == null) {
					continue;
				}
				
				try {
					Class attrClass  = this.getAttributeClassFromAttributeName(fullname);
					buf.append("  class = " + attrClass.getCanonicalName() + "\n");
				} catch (ModelMapException e) {
					e.printStackTrace();
				}
		
				try {
					Class ieClass = this.getInformationEntityClassFromAttributeName(fullname);
					buf.append("  parent class = " + ieClass.getCanonicalName() + "\n");
				} catch (ModelMapException e) {
					e.printStackTrace();
				}
		
				try {
					Iterator getterIter = this.getGettersFromAttributeName(fullname).iterator();
					while (getterIter.hasNext()) {
						Method m = Method.class.cast(getterIter.next());
						buf.append("  getter = " + m.getReturnType().getCanonicalName() + " " + m.getName() + " " + m.getParameterTypes().getClass().getCanonicalName() + "\n");
					}
				} catch (ModelMapException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				try {
					Iterator setterIter = this.getSettersFromAttributeName(fullname).iterator();
					while (setterIter.hasNext()) {
						Method m = Method.class.cast(setterIter.next());
						buf.append("  setter = " + m.getReturnType().getCanonicalName() + " " + m.getName() + " " + m.getParameterTypes().getClass().getCanonicalName() + "\n");
					}
				} catch (ModelMapException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
		
		buf.append("\nChild fields and accessor methods: \n");
		Iterator<Class> parentIter = this.getInformationEntityChildrenMap().keySet().iterator();
		while (parentIter.hasNext()) {
			f = null;
			Class parent = parentIter.next();
			
			buf.append("class = " + parent.getCanonicalName() + "\n");
			Iterator<Field> cihldFieldIter = null;
			try {
				cihldFieldIter = this.getChildFieldsFromClass(parent).iterator();
			} catch (ModelMapException e1) {
				e1.printStackTrace();
			}
			
			while (cihldFieldIter.hasNext()) {
				f = cihldFieldIter.next();
				buf.append("  field = " + f + "\n");
				try {
					Iterator<Method> getterIter = this.getGettersFromField(f).iterator();
					while (getterIter.hasNext()) {
						Method m = getterIter.next();
						buf.append("    getter = " + m.getName() + "\n");						
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (ModelMapException e) {
					e.printStackTrace();
				}
			}
		}
		
		buf.append("\nLineages: \n");
		Iterator<Class> classIter = this.childToParentMap.keySet().iterator();
		Class currClass = null;
		while (classIter.hasNext()) {
			currClass = classIter.next();
			try {
				buf.append("lineage for " + currClass.getCanonicalName() + " : " + this.getLineage(currClass) + "\n");
			} catch (ModelMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return buf.toString();
	}

	private void createModelMapPropertySkeleton(String filename) {
		try {
			FileWriter fw = new FileWriter(filename);
			Collection<String> keySet = this.attributeNameToFieldMap.keySet();
			String[] keys = new String[keySet.size()];
			keys = keySet.toArray(keys);
			
			java.util.Arrays.sort(keys);
			
			for (int i = 0; i < keys.length; i++) {
				fw.write(String.class.cast(keys[i]));
				fw.write("=\n");
			}
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	public HashMap<String, Class> getInformationEntityMap() {
		return nameToClassMap;
	}

	public Class getModelBaseClass() {
		return modelBaseClass;
	}

	public HashMap<Class, HashSet<Field>> getInformationEntityChildrenMap() {
		return parentToChildrenMap;
	}
}
