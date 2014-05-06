/**
 *
 */
package gov.nih.nci.ivi.dicom;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.modelmap.ModelDictionary;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.xml.sax.InputSource;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.AttributeTagAttribute;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.ValueRepresentation;

/**
 * @author rutt, tpan this class converts cql to pixelmed attribute list. First
 *         implementation adheres to the following query constraints. 1. target
 *         is one of the following: patient, study, series, image 2. for
 *         patient, study, series, and image, only DICOM attributes is returned
 *         The following restrictions are placed on the CQL for DICOM. 1. to
 *         specify a DICOM attribute to retrieve but not to filter, use "" for
 *         the attribute value 2. supports only AND logical operation 3.
 *         supports only EQUAL_TO attribute predicate 4. The association HAS to
 *         be relative to the target. 5. association and attributes should be
 *         GROUPED, unless there is only one. 6. each cql object (association
 *         and target) should have at most 2 associations, one to parent, one to
 *         child 7. target is one of following: patient, study, series, image 8.
 *         the schema needs to have bidirectional links up and down the dicom
 *         hierarchy. 9. attribute name should be in Capital case. and the
 *         abbreviations should be all caps. 10. the cQL does not specify the
 *         query retrieve level, other than by the target. 11.
 *         queryRetrieveLevel for a retrieve should be determined by the finest
 *         grain UID in the CQL. this is necessary since we may want to
 *         materialize stuff at a different level than what we are filtering and
 *         PACS need the right filter.
 */
// the queryRetrieveDepth was not set properly. It was depending on the
// attributes in CQL, which would be misleading
// change it to depend on association depth.
public class CQL2DICOM {
    private static Logger myLogger = LogManager.getLogger(CQL2DICOM.class);

    public int debug = 0;
    private InformationEntity queryRetrieveDepth = null;
    private static DicomDictionary dict = new DicomDictionary();
    private ModelDictionary mdict = null;
    private ModelMap mapping = null;

    public CQL2DICOM(ModelMap mapping) throws ModelMapException {
        if (mapping == null) {
            throw new ModelMapException("Null Model Map ");
        }

        this.mapping = mapping;
        this.mdict = this.mapping.getModelDict();
    }

    // this function converts a CQL query to an attributeList
    // this just does a straight translation, and also checks to make sure the
    // attributes are supported
    // if not, throw an exception.
    // any dicom hierarchy specific decisions should be left at the CQL query
    // processor level.
    //
    // the required fields are filled in automatically, based on the model
    // attributes
    public AttributeList cqlToPixelMed(CQLQuery cqlQuery) throws MalformedQueryException, DicomException,
            ModelMapException {
        // get the target and specify the right attribute for retrieval
        if (myLogger.isDebugEnabled()) {
            try {
                QName cqlQueryQName = new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery");
                myLogger.debug(ObjectSerializer.toString(cqlQuery, cqlQueryQName));
            } catch (SerializationException e2) {
                myLogger.warn("Error serializing query for debug output.", e2);
            }
        }

        if (cqlQuery == null) {
            throw new MalformedQueryException("null query specified");
        }

        gov.nih.nci.cagrid.cqlquery.Object target = cqlQuery.getTarget();
        if (target == null || target.getName().equals("")) {
            throw new MalformedQueryException("missing CQL query target");
        }
        if (!this.mapping.getModelDict().getInformationEntityMap().containsKey(target.getName().trim())) {
            throw new MalformedQueryException("invalid CQL query target for this model");
        }

        try {
            updateQueryRetrieveDepth(target);
        } catch (ClassNotFoundException e1) {
            throw new MalformedQueryException("target class is not correct");
        }

        SpecificCharacterSet charSet = new SpecificCharacterSet(new String[1]);

        AttributeList filter = new AttributeList();

        gov.nih.nci.cagrid.cqlquery.Association assoc = target.getAssociation();
        gov.nih.nci.cagrid.cqlquery.Attribute attr = target.getAttribute();
        gov.nih.nci.cagrid.cqlquery.Group group = target.getGroup();

        int count = 0;
        if (attr != null) {
            count++;
        }
        if (assoc != null) {
            count++;
        }
        if (group != null) {
            count++;
        }
        if (count > 1) {
            throw new MalformedQueryException("too many members");
        }

        // this parses all the attributes out from the cql and put then into
        // pixelmed attribute lists
        if (attr != null) {
            handleAttr(attr, target, filter, charSet);
        } else if (assoc != null) {
            handleAssoc(assoc, filter, charSet);
        } else if (group != null) {
            handleGroup(group, target, filter, charSet);
        }

        Class<?> targetClass = null;
        try {
            targetClass = Class.forName(target.getName());
        } catch (ClassNotFoundException e) {
            String targetClassName = target.getName();
            myLogger.error("Could not get Java class for representation of query target: " + targetClassName, e);
            throw new MalformedQueryException("Unable to process query because target Java class could not be found: "
                    + targetClassName);
        }

        // fill in any other fields for the target class. list is from the model
        // also put the parent classes's attributes in.
        Class parent = targetClass;
        while (parent != null) {
	        Collection<String> attributeNames = mdict.getAttributeNamesFromInformationEntityClass(parent);
	        // convert each to dicom attribute
	        Iterator<String> iter = attributeNames.iterator();
	        String attributeName = null;
	        while (iter.hasNext()) {
	            attributeName = String.class.cast(iter.next());
	            AttributeTag t = mapping.getAttributeTagFromModelAttributeName(attributeName);
	            if (t != null && filter.get(t) == null) {
	                byte[] vr = dict.getValueRepresentationFromTag(t);
	                Attribute a = com.pixelmed.dicom.AttributeFactory.newAttribute(t, vr);
	                filter.put(t, a);
	            }
	        }
	        parent = mdict.getParentClassFromInformationEntityClass(parent);
        }
//        // put the parent classes's attributes in.
//        Iterator<?> iter2 = this.mapping.getRetrieveLevelIterator();
//        while (iter2.hasNext()) {
//            InformationEntity ie = InformationEntity.class.cast(iter2.next());
//
//            //System.out.println("current ie = " + ie.toString() + " target = "
//            // + this.queryRetrieveDepth.toString());
//            if (this.queryRetrieveDepth.compareTo(ie) >= 0) {
//                // the target depth is deeper than the current IE.  So add the current IE to the list.
//
//                if (myLogger.isDebugEnabled()) {
//                    myLogger.debug("current ie = " + ie.toString() + " target = " + this.queryRetrieveDepth.toString());
//                }
//                
//                AttributeTag t = this.mapping.getRetrieveLevelRequiredTag(this.mapping.getRetrieveLevel(ie));
//                if (filter.get(t) == null) {
//                    // System.out.println("current ie = " + ie.toString() +
//                    // " target = " + this.queryRetrieveDepth.toString());
//
//                    byte[] vr = dict.getValueRepresentationFromTag(t);
//                    Attribute a = com.pixelmed.dicom.AttributeFactory.newAttribute(t, vr);
//                    filter.put(t, a);
//                }
//            }
//        }

        // put the query retrieve level in
        AttributeTag tag = TagFromName.QueryRetrieveLevel;
        byte[] vr = CQL2DICOM.dict.getValueRepresentationFromTag(tag);
        Attribute qlAttr = com.pixelmed.dicom.AttributeFactory.newAttribute(tag, vr);
        qlAttr.addValue(this.mapping.getRetrieveLevel(this.queryRetrieveDepth));
        filter.put(TagFromName.QueryRetrieveLevel, qlAttr);

        return filter;
    }

    private void appendDicomExpr(String[] triplet, String containerObjectName, AttributeList filter,
            SpecificCharacterSet specificCharacterSet) throws MalformedQueryException, DicomException {

        assert (triplet[0] != null);
        assert (triplet[1] != null);
        assert (triplet[2] != null);

        if (!triplet[1].equals("EQUAL_TO")) {
            throw new MalformedQueryException("currently only support EQUAL_TO predicate");
        }

        // convert from model name to pixelmed name
        AttributeTag t = null;
        String attributeName = containerObjectName + "." + triplet[0];
        try {
            t = mapping.getAttributeTagFromModelAttributeName(attributeName);
        } catch (ModelMapException e) {
            myLogger.error("Error converting model attribute name to dicom attribute tag: " + attributeName, e);
        }

        if (t == null) {
            throw new DicomException("cannot create tag from name " + attributeName);
        }

        Attribute a;
        byte[] vr = dict.getValueRepresentationFromTag(t);
        if (filter.get(t) != null) {
            a = filter.get(t);
            filter.remove(t);
        } else {
            a = com.pixelmed.dicom.AttributeFactory.newAttribute(t, vr);
        }

        boolean charsetSpecific = ValueRepresentation.isAffectedBySpecificCharacterSet(vr);
        String charsetConvertedValue = null;
        if (charsetSpecific) {
            byte[] bytes = triplet[2].getBytes();
            charsetConvertedValue = specificCharacterSet.translateByteArrayToString(bytes, 0, bytes.length);
        }

        // TODO: can we simplify this?

        // Strings
        if (ValueRepresentation.isAgeStringVR(vr) || ValueRepresentation.isApplicationEntityVR(vr)
                || ValueRepresentation.isCodeStringVR(vr) || ValueRepresentation.isDateVR(vr)
                || ValueRepresentation.isDateTimeVR(vr) || ValueRepresentation.isDecimalStringVR(vr)
                || ValueRepresentation.isIntegerStringVR(vr) || ValueRepresentation.isTimeVR(vr)
                || ValueRepresentation.isUniqueIdentifierVR(vr)) {
            a.addValue(triplet[2]);

            // string affected by characterset
        } else if (ValueRepresentation.isLongStringVR(vr) || ValueRepresentation.isShortStringVR(vr)
                || ValueRepresentation.isPersonNameVR(vr)) {
            a.addValue(charsetConvertedValue);

            // other non text
        } else if (ValueRepresentation.isAttributeTagVR(vr)) {
            AttributeTagAttribute a2 = AttributeTagAttribute.class.cast(a);
            a2.addValue(dict.getTagFromName(triplet[2]));

        } else if (ValueRepresentation.isFloatDoubleVR(vr)) {
            a.addValue(Double.parseDouble(triplet[2]));

        } else if (ValueRepresentation.isFloatSingleVR(vr)) {
            a.addValue(Float.parseFloat(triplet[2]));

        } else if (ValueRepresentation.isOtherByteVR(vr)) {
            // assume the byte array is in the string, as base 64 encoded. need
            // even length
            // sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            throw new DicomException("unimplemented " + vr);

        } else if (ValueRepresentation.isOtherFloatVR(vr)) {
            throw new DicomException("unimplemented " + vr);

        } else if (ValueRepresentation.isOtherWordVR(vr)) {
            throw new DicomException("unimplemented " + vr);

        } else if (ValueRepresentation.isSequenceVR(vr)) {
            throw new DicomException("unimplemented " + vr);

        } else if (ValueRepresentation.isSignedLongVR(vr)) {
            a.addValue(Long.parseLong(triplet[2]));

        } else if (ValueRepresentation.isSignedShortVR(vr)) {
            a.addValue(Short.parseShort(triplet[2]));

        } else if (ValueRepresentation.isUnsignedLongVR(vr)) {
            throw new DicomException("unimplemented " + vr);

        } else if (ValueRepresentation.isUnsignedShortVR(vr)) {
            a.addValue((short) Long.parseLong(triplet[2]));

            // text attribute
        } else if (ValueRepresentation.isLongTextVR(vr) || ValueRepresentation.isShortTextVR(vr)
                || ValueRepresentation.isUnlimitedTextVR(vr)) {
            a.addValue(charsetConvertedValue);

        } else if (ValueRepresentation.isUnknownVR(vr)) {
            throw new DicomException("unknown vr " + vr);
            // these do not appear to be in pixelmed.
        } else if (ValueRepresentation.isOtherUnspecifiedVR(vr)) {
            throw new DicomException("unimplemented " + vr);
        } else if (ValueRepresentation.isOtherByteOrWordVR(vr)) {
            throw new DicomException("unimplemented " + vr);
        } else if (ValueRepresentation.isShortValueLengthVR(vr)) {
            throw new DicomException("unimplemented " + vr);
        } else if (ValueRepresentation.isUnspecifiedShortOrOtherWordVR(vr)) {
            throw new DicomException("unspecified short or other word " + vr);
        } else if (ValueRepresentation.isUnspecifiedShortVR(vr)) {
            throw new DicomException("unspecified short " + vr);
        }

        filter.put(t, a);

    }

    private void handleAttr(gov.nih.nci.cagrid.cqlquery.Attribute attr, gov.nih.nci.cagrid.cqlquery.Object parent,
            AttributeList filter, SpecificCharacterSet specificCharacterSet) throws MalformedQueryException,
            DicomException {

        String name = attr.getName().trim();
        String pred = attr.getPredicate().toString().trim();
        String val = attr.getValue().trim();

        if (name == null) {
            throw new MalformedQueryException("CQL2DICOM: attribute needs a name");
        }
        if (pred == null) {
            throw new MalformedQueryException("CQL2DICOM: attribute needs a predicate");
        }
        if (val == null) {
            throw new MalformedQueryException("CQL2DICOM: attribute needs a value");
        }
        String[] out = new String[3];
        out[1] = pred;
        out[2] = val;
        out[0] = name;
        appendDicomExpr(out, parent.getName(), filter, specificCharacterSet);
    }

    private void handleAssoc(gov.nih.nci.cagrid.cqlquery.Association assoc, AttributeList filter,
            SpecificCharacterSet specificCharacterSet) throws MalformedQueryException, DicomException {

        String roleName = assoc.getRoleName().trim();
        String name = assoc.getName().trim();
        if (roleName == null) {
            throw new MalformedQueryException("CQL2DICOM: assoc needs a roleName");
        }
        if (name == null) {
            throw new MalformedQueryException("CQL2DICOM: assoc needs a name");
        }

        if (!this.mapping.getModelDict().getInformationEntityMap().containsKey(name)) {
            throw new MalformedQueryException("invalid CQL query assoc " + name + " for this model");
        }

        gov.nih.nci.cagrid.cqlquery.Association assoc2 = assoc.getAssociation();
        gov.nih.nci.cagrid.cqlquery.Attribute attr = assoc.getAttribute();
        gov.nih.nci.cagrid.cqlquery.Group group = assoc.getGroup();

        int count = 0;
        if (attr != null) {
            count++;
        }
        if (assoc2 != null) {
            count++;
        }
        if (group != null) {
            count++;
        }
        if (count == 0) {
            throw new MalformedQueryException("no members");
        } else if (count > 1) {
            throw new MalformedQueryException("too many members");
        }

        if (attr != null) {
            handleAttr(attr, assoc, filter, specificCharacterSet);
        } else if (assoc2 != null) {
            handleAssoc(assoc2, filter, specificCharacterSet);
        } else if (group != null) {
            handleGroup(group, assoc, filter, specificCharacterSet);
        }

        // TCP: removed to prevent the problem of setting the query retrieve
        // level to the deepest association rather than the target's
        // try {
        // updateQueryRetrieveDepth(assoc);
        // } catch (ClassNotFoundException e1) {
        // throw new MalformedQueryException("assoc class is not correct");
        // } catch (ModelMapException e) {
        // throw new MalformedQueryException("problem with model map");
        // }
    }

    private void updateQueryRetrieveDepth(gov.nih.nci.cagrid.cqlquery.Object obj) throws ClassNotFoundException,
            ModelMapException {

        // we are looking at the information entity level of the attribute tag.
        // and decide what is the deepest level of query retrieve.
        Class<?> model = Class.forName(obj.getName().trim());
        InformationEntity ie = mapping.getInformationEntityFromModelClass(model);
        if (ie == null) {
            return;
        }

        if (this.queryRetrieveDepth == null) {
            this.queryRetrieveDepth = ie;
        } else if (ie.compareTo(this.queryRetrieveDepth) > 0) {
            this.queryRetrieveDepth = ie;
        }
    }

    private void handleGroup(gov.nih.nci.cagrid.cqlquery.Group group, gov.nih.nci.cagrid.cqlquery.Object parent,
            AttributeList filter, SpecificCharacterSet specificCharacterSet) throws MalformedQueryException,
            DicomException {
        if (group.getLogicRelation() == null) {
            throw new MalformedQueryException("group needs a logical operator");
        }

        /*
         * ASHISH: Needs to be removed since we now support OR operation if
         * (!group
         * .getLogicRelation().equals(gov.nih.nci.cagrid.cqlquery.LogicalOperator
         * .AND)) { throw new MalformedQueryException("group logical operator "
         * + group.getLogicRelation() + " is not supported."); }
         */

        // next go through the list of attributes
        gov.nih.nci.cagrid.cqlquery.Attribute[] attributes = group.getAttribute();
        if (attributes != null && attributes.length > 0) {
            for (int i = 0; i < attributes.length; i++) {
                handleAttr(attributes[i], parent, filter, specificCharacterSet);
            }
        }

        // next go through the list of groups
        gov.nih.nci.cagrid.cqlquery.Group[] groups = group.getGroup();
        if (groups != null && groups.length > 0) {
            for (int i = 0; i < groups.length; i++) {
                handleGroup(groups[i], parent, filter, specificCharacterSet);
            }
        }

        // next go through the list of associations
        gov.nih.nci.cagrid.cqlquery.Association[] associations = group.getAssociation();
        if (associations != null && associations.length > 0) {
            for (int i = 0; i < associations.length; i++) {
                handleAssoc(associations[i], filter, specificCharacterSet);
            }
        }
    }

    // get the deepest level for the information entity based on a list of model
    // attribute names
    @Deprecated
    private InformationEntity getDeepestDicomInformationEntityFromAttributeList(AttributeList attrs) {
        if (attrs == null) {
            return null;
        }
        if (attrs.size() == 0) {
            return null;
        }
        Iterator<AttributeTag> iter = attrs.keySet().iterator();

        InformationEntity deepest = null;

        while (iter.hasNext()) {
            AttributeTag tag = iter.next();

            InformationEntity current = this.dict.getInformationEntityFromTag(tag);
            if (current == null) {
                if (debug == 2)
                    System.out.println("WARNING: no information entity associated: " + this.dict.getNameFromTag(tag)
                            + " " + tag.toString());
                continue;
            }
            if (deepest == null || current.compareTo(deepest) > 0) {
                deepest = current;
            }
        }

        return deepest;
    }

    String getQueryRetrieveLevelAsString() {
        return queryRetrieveDepth.toString();
    }

    InformationEntity getQueryRetrieveDepth() {
        return queryRetrieveDepth;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        com.pixelmed.dicom.DicomDictionary dict = new com.pixelmed.dicom.DicomDictionary();
        ModelMap map = null;
        try {
            map = new ModelMap();
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        CQL2DICOM cql2 = null;
        try {
            cql2 = new CQL2DICOM(map);
        } catch (ModelMapException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // load the cqlquery

        String path = "C:\\Data\\src\\middleware\\projects\\iviCore\\resources";
        String[] filenames = new String[] { "nciaCQL2-patient.xml", "nciaCQL2-study.xml", "nciaCQL2-series.xml",
                "nciaCQL1.xml", "nciaCQL3.xml" };

        for (int i = 0; i < filenames.length; i++) {
            String filename = path + File.separator + filenames[i];
            try {
                InputSource queryInput = new InputSource(new FileReader(filename));
                CQLQuery query = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);

                FileInputStream fis = new FileInputStream(filename);
                byte[] cqlString = new byte[fis.available()];
                fis.read(cqlString);
                System.out.println("cqlquery =\n" + XMLUtilities.formatXML(new String(cqlString)));

                AttributeList filter = cql2.cqlToPixelMed(query);
                System.out.println("xpath =\n" + filter.toString(dict));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
