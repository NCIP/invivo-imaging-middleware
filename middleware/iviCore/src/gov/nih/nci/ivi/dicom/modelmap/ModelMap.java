/**
 *
 */
package gov.nih.nci.ivi.dicom.modelmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Pattern;

import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.TagFromName;

/**
 * @author tpan
 *
 */
public class ModelMap {

	public int debug = 0;

	private static Pattern pat= Pattern.compile("\\(0x[0-9a-fA-F]{4},0x[0-9a-fA-F]{4}\\)");

	public static final String IMAGE_STR = "IMAGE";
	public static final String SERIES_STR = "SERIES";
	public static final String STUDY_STR = "STUDY";
	public static final String PATIENT_STR = "PATIENT";
	private static final String TOP_LEVEL_CLASS_KEY = "top_level_class";
	private static final String HIERARCHY_PROPERTIES = "hierarchy_properties";
	public static final int NON_ARRAY_INDEX = -1;

	HashMap<InformationEntity, String> retrieveLevels = null;
	HashMap<String, AttributeTag> retrieveLevelRequiredTags = null;
	HashMap<String, Class> retrieveLevelClasses = null;

	Properties fieldToDicomTagMap = new Properties();
	// have to allow the same dicom tag to map to multiple model attributes
	HashMap<String, HashMap<String, Integer>> dicomTagToModelMap = null;
	HashMap<Class, InformationEntity> classToIEMap;
	ModelDictionary modelDict;
	DicomDictionary dicomDict;

	private void initializeRetrieveLevels() {
		// TODO: replace this with the map's information entity stuff
		if (this.retrieveLevels == null) {
			this.retrieveLevels = new HashMap<InformationEntity, String>();
			this.retrieveLevels.put(InformationEntity.PATIENT, PATIENT_STR);
			this.retrieveLevels.put(InformationEntity.STUDY, STUDY_STR);
			// procedure step's attributes are at the series level, so need to retrieve multiple series to get a procedure step.  just retrieve study
			this.retrieveLevels.put(InformationEntity.PROCEDURESTEP, STUDY_STR);
			this.retrieveLevels.put(InformationEntity.SERIES, SERIES_STR);
			// concatenation's attributes are at the image level, so need to retrieve multiple images to get a procedure step.  just retrieve series
			this.retrieveLevels.put(InformationEntity.CONCATENATION, SERIES_STR);
			// instance should be at image level.  works with series in vPACS.  will need a tool to test if want to try with image.
			this.retrieveLevels.put(InformationEntity.INSTANCE, IMAGE_STR);
//******			ModelMap.retrieveLevels.put(InformationEntity.INSTANCE, "SERIES");
			this.retrieveLevels.put(InformationEntity.FRAME, IMAGE_STR);

		}
		if (this.retrieveLevelRequiredTags == null) {
			this.retrieveLevelRequiredTags = new HashMap<String, AttributeTag>();
			this.retrieveLevelRequiredTags.put(PATIENT_STR, TagFromName.PatientID);
			this.retrieveLevelRequiredTags.put(STUDY_STR, TagFromName.StudyInstanceUID);
			this.retrieveLevelRequiredTags.put(SERIES_STR, TagFromName.SeriesInstanceUID);
			this.retrieveLevelRequiredTags.put(IMAGE_STR, TagFromName.SOPInstanceUID);
		}
		// to avoid hard coded construction of the mapping to java beans,
		// the following code has been made dynamic.  slower, but should only have to be
		// done once per map instance.
		if (this.retrieveLevelClasses == null) {
			this.retrieveLevelClasses = new HashMap<String, Class>();

			Iterator<String> iter = this.retrieveLevelRequiredTags.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				AttributeTag tag = this.retrieveLevelRequiredTags.get(key);

				// now get the model attribute from the tag (assume attributes all belong to the same class)
				HashMap<String, Integer> modelAttrNames = null;
				try {
					modelAttrNames = this.getModelAttributeNamesFromAttributeTag(tag);
				} catch (ModelMapException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Iterator<String> iter2 = modelAttrNames.keySet().iterator();
				if (iter2.hasNext()) {
					String modelAttrName = iter2.next();
					int index = modelAttrName.lastIndexOf('.');

					String className = modelAttrName.substring(0, index);
					// get the class name.  if the attr name ends with _value,
					// then this is a whole class that's mapped to a dicom attribute.  get ITS parent
					// otherwise, get the attribute's parent
					if (modelAttrName.endsWith("_value")) {
						try {
							Class parent = this.modelDict.getParentClassFromInformationEntityClass(Class.forName(className));
							className = parent.getCanonicalName();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (ModelMapException e) {
							e.printStackTrace();
						}
					}

					try {
						this.retrieveLevelClasses.put(key, Class.forName(className));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public Iterator getRetrieveLevelIterator() {
		return this.retrieveLevels.keySet().iterator();
	}

	public Iterator getRetrieveKeyIterator() {
		return this.retrieveLevelRequiredTags.values().iterator();
	}

	public String getRetrieveLevel(InformationEntity ie) throws ModelMapException {
		if (ie == null) {
			throw new ModelMapException("InformationEntity paramter is null");
		}
		return this.retrieveLevels.get(ie);
	}

	public AttributeTag getRetrieveLevelRequiredTag(String queryRetrieveLevel) throws ModelMapException {
		if (queryRetrieveLevel == null) {
			throw new ModelMapException("queryRetrieveLevel paramter is null");
		}
		return this.retrieveLevelRequiredTags.get(queryRetrieveLevel.trim().toUpperCase());
	}

	public Class getModelClassFromQueryRetrieveLevel(String queryRetrieveLevel) throws ModelMapException {
		if (queryRetrieveLevel == null) {
			throw new ModelMapException("queryRetrieveLevel paramter is null");
		}
		return this.retrieveLevelClasses.get(queryRetrieveLevel);
	}

	private int[] getGroupElementFromTagString(String dicomTag) throws ModelMapException {
		if (dicomTag == null) {
			throw new ModelMapException("tag is missing");
		}

		if (!pat.matcher(dicomTag).matches()) {
			return new int[] { -1, -1 };
		}

		dicomTag = dicomTag.substring(dicomTag.indexOf("(")+1, dicomTag.lastIndexOf(")"));
		int sepIndex = dicomTag.indexOf(',');
		String groupStr = dicomTag.substring(0, sepIndex);
		groupStr = groupStr.substring(groupStr.indexOf('x')+1);
		String elementStr = dicomTag.substring(sepIndex+1);
		elementStr = elementStr.substring(elementStr.indexOf('x')+1);
		int[] result = new int[2];
		result[0] = Integer.parseInt(groupStr, 16);
		result[1] = Integer.parseInt(elementStr, 16);

		return result;
	}


	public ModelMap() throws FileNotFoundException, ModelMapException, IOException, ClassNotFoundException {
		ClassLoader cl = ModelMap.class.getClassLoader();
		InputStream is = cl.getResourceAsStream("resources/modelmap/NCIAModelMap.properties");

		InputStream is2 = cl.getResourceAsStream("resources/modelmap/NCIAModelHierarchy.properties");

		fieldToDicomTagMap.load(is);

		String topclass = fieldToDicomTagMap.getProperty(TOP_LEVEL_CLASS_KEY);
		fieldToDicomTagMap.remove(TOP_LEVEL_CLASS_KEY);

		fieldToDicomTagMap.remove(HIERARCHY_PROPERTIES);

		modelDict = new ModelDictionary();


		this.initialize(topclass);

	}


	public ModelMap(File mapFile) throws FileNotFoundException, ModelMapException, IOException, ClassNotFoundException {
		if (mapFile == null) {
			throw new ModelMapException("null property file name");
		}
		if (!mapFile.exists()) {
			throw new FileNotFoundException("file " + mapFile + " does not exist");
		}
		if (!mapFile.canRead()) {
			throw new IOException("file " + mapFile + " cannot be read");
		}

		fieldToDicomTagMap.load(new FileInputStream(mapFile));

		String topclass = fieldToDicomTagMap.getProperty(TOP_LEVEL_CLASS_KEY);
		fieldToDicomTagMap.remove(TOP_LEVEL_CLASS_KEY);

		String hierarchyFilename = fieldToDicomTagMap.getProperty(HIERARCHY_PROPERTIES);
		fieldToDicomTagMap.remove(HIERARCHY_PROPERTIES);

		if (debug == 1) System.out.println("loading " + hierarchyFilename);

		modelDict = new ModelDictionary(new File(mapFile.getParent() + File.separator + hierarchyFilename));

		this.initialize(topclass);
	}


	private void initialize(String topclass) throws ModelMapException, IOException, ClassNotFoundException {
		modelDict.initialize(topclass);

		dicomDict = new DicomDictionary();

		//System.out.println(fieldToDicomTagMap.toString().replace(", ","\n "));

		// create the reverse mapping
		classToIEMap = createModelClassToInformationEntityMap();
		dicomTagToModelMap = createDicomTagToModelMap(this.fieldToDicomTagMap);

		initializeRetrieveLevels();
	}


	// this function now allows 1 tag to map to model attributes from multiple classes, thus
	// a 1 to n non-unique mapping.  It also now stores the hashmap mapping to avoid some query time.
	private HashMap<String, HashMap<String,Integer>> createDicomTagToModelMap(Properties map) throws ModelMapException {
		HashMap<String, HashMap<String,Integer>> result = new HashMap<String, HashMap<String,Integer>>();

		synchronized(map) {
			Iterator iter = map.keySet().iterator();
			int pos = -1;
			int index = -1;
			String dicomTag = "";
			String key = "";
			while (iter.hasNext()) {
				key = String.class.cast(iter.next());
				dicomTag = map.getProperty(key);

				pos = dicomTag.lastIndexOf('.');
				index = -1;
				if (pos >= 0) {
					if (debug == 1) System.out.println(dicomTag);
					index = Integer.parseInt(dicomTag.substring(pos+1));
					dicomTag = dicomTag.substring(0,pos);
				}

				if (!result.containsKey(dicomTag) || result.get(dicomTag) == null) {
					result.put(dicomTag, new HashMap<String,Integer>());
				}

				// TODO: annotation and generalSeries both map to the same IE, which is Series.  and both
				//     contain seriesInstanceUID.  thus, we get 2 copies.
				//   can't just get the modelclass from ie, since thier ie is derived from the deepest IE
				//   of the attributes. May need to create another mapping.
				Class modelClass = this.modelDict.getInformationEntityClassFromAttributeName(key);
				//if (key.endsWith("studyInstanceUID")) System.out.println("modelClass: " + modelClass.getCanonicalName());
				InformationEntity modelIE = this.getInformationEntityFromModelClass(modelClass);
				//if (key.endsWith("studyInstanceUID")) System.out.println("modelIE " + modelIE.toString());
				int[] groupElement = this.getGroupElementFromTagString(dicomTag);
				//if (key.endsWith("studyInstanceUID")) System.out.println("dicom Tag : " + dicomTag);
				//if (key.endsWith("studyInstanceUID")) System.out.println("group elements : " + groupElement[0] + " " + groupElement[1]);
				AttributeTag tag = new AttributeTag(groupElement[0], groupElement[1]);
				InformationEntity tagIE = this.dicomDict.getInformationEntityFromTag(tag);
				//if (key.endsWith("studyInstanceUID")) System.out.println("attributeTag = " + this.dicomDict.getNameFromTag(tag));
				//if (key.endsWith("studyInstanceUID")) System.out.println("tagIE " + tagIE.toString());

				if (modelIE != null && tagIE != null && modelIE.equals(tagIE)) {
					result.get(dicomTag).put(key, index);
					//if (key.endsWith("studyInstanceUID")) System.out.println("put into map");
				}
			}
		}
		return result;
	}

	// create the IE map.  there cannot be null IEs mapped to model class as DICOM2Model
	// requires this.
	private HashMap<Class, InformationEntity> createModelClassToInformationEntityMap() {
		Iterator<String> iter = this.modelDict.getInformationEntityNameIterator();
		HashMap<Class, InformationEntity> result = new HashMap<Class, InformationEntity>();

		while (iter.hasNext()) {
			String modelClassName = iter.next();
			Class modelClass = this.modelDict.getInformationEntityMap().get(modelClassName);

			try {
				// this allows a class defined in the model to be mapped to DICOM's attribute tags.  The class can
				// attributes that comes from a mixture of DICOM Information Entities.  Therefore we need to go as deep
				// as we possibly can.
				HashSet<String> attrNames = this.modelDict.getAttributeNamesFromInformationEntityClass(modelClass);
				InformationEntity ie = this.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);

				result.put(modelClass, ie);
			} catch (ModelMapException e) {
				e.printStackTrace();
			}
		}

		// walk through to check for null information entities.
		// set the root class to patient if it's null ie.
		Class root = this.modelDict.getModelBaseClass();
		if (result.get(root) == null) {
			try {
				HashSet<String> attrNames = this.modelDict.getAttributeNamesFromInformationEntityClass(root);
				InformationEntity ie = this.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);

				result.put(root, ie);
			} catch (ModelMapException e) {
				e.printStackTrace();
			}
		}

		// deal with null IE.  If there is a paprent, then set the current's information entity to parent.  else set to Patient.
		Iterator<Class> classes = result.keySet().iterator();
		while (classes.hasNext()) {
			Class cl = classes.next();
			InformationEntity ie = result.get(cl);
			Class parent = cl;
			while (ie == null) {
				try {
					parent = this.modelDict.getParentClassFromInformationEntityClass(parent);
				} catch (ModelMapException e) {
					parent = null;
				}
				if (parent == null) {
					break;
				} else {
					ie = result.get(parent);
				}
			}
			result.put(cl, ie);
		}

		return result;
	}

	public InformationEntity getInformationEntityFromModelClass(Class cl) throws ModelMapException {
		if (cl == null) {
			throw new ModelMapException("model is null");
		}
		return classToIEMap.get(cl);
	}

	// get the deepest level for the information entity based on a list of model attribute names
	public InformationEntity getDeepestDicomInformationEntityFromModelAttributeNames(HashSet<String> attrNames) throws ModelMapException {
		if (attrNames == null) {
			throw new ModelMapException("attribute names should not be null");
		}
		if (attrNames.size() == 0) {
			return null;
		}
		Iterator<String> iter = attrNames.iterator();

		InformationEntity deepest = null;

		while (iter.hasNext()) {
			String attrName = iter.next();

			AttributeTag tag = this.getAttributeTagFromModelAttributeName(attrName);
			if (tag == null) {
				if (debug == 2) System.out.println("WARNING: no tag associated: " + attrName + ". ignored.");
				continue;
			}

			InformationEntity current = this.dicomDict.getInformationEntityFromTag(tag);
			if (current == null) {
				if (debug == 2) System.out.println("WARNING: no information entity associated: " + attrName + " " + dicomDict.getNameFromTag(tag) + " " + tag.toString());
				continue;
			}
			if (deepest == null || current.compareTo(deepest) > 0) {
				deepest = current;
			}
		}

		return deepest;
	}


	// this is for fields that are sequences, such as patient orientation, position, etc
	public int getArrayIndexFromModelAttributeName(String attrName) throws ModelMapException {
		if (attrName == null) {
			throw new ModelMapException("null parameter");
		}
		if (attrName.equals("")){
			throw new ModelMapException("empty string parameter");
		}
		String dicomTag = fieldToDicomTagMap.getProperty(attrName);

		if (dicomTag == null) {
			if (!attrName.endsWith(".id")) {
				throw new ModelMapException("attrname does not exist in the map " + attrName);
			} else {
				System.out.println("attrname is an id " + attrName);
				return -1;
			}
		}

		
		// the dicom tag string in our format is the dicomtag + "." + arrayIndex.
		int idx = dicomTag.lastIndexOf('.');
		if (idx >= 0) {
			String intString = dicomTag.substring(idx + 1);
			return Integer.parseInt(intString);
		} else {
			return -1;
		}
	}



	// the attribute name is fully qualified.  e.g. xxxx.PT_NAME or xxx.StudyInstanceUIDType._value
	public AttributeTag getAttributeTagFromModelAttributeName(String attrName) throws ModelMapException {
		if (attrName == null) {
			throw new ModelMapException("null parameter");
		}
		if (attrName.equals("")){
			throw new ModelMapException("empty string parameter");
		}
		String dicomTag = fieldToDicomTagMap.getProperty(attrName);

		// TODO handle attributename that are not fully qualified

		if (dicomTag == null || !dicomTag.startsWith("(0x")) {
			return null;
		}

		int idx = dicomTag.lastIndexOf('.');
		if (idx >= 0) {
			dicomTag = dicomTag.substring(0, idx);
		}

		int[] groupElement = this.getGroupElementFromTagString(dicomTag);

		// check to see if this tag is private
		AttributeTag tag = new AttributeTag(groupElement[0], groupElement[1]);
		if (tag.isPrivate()) {

			if (debug == 2) System.out.println("WARNING: private tag: " + tag.toString() + ". ignored.");
			return null;
		}

		return tag;
	}


	public HashMap<String, Integer> getModelAttributeNamesFromAttributeTag(AttributeTag tag) throws ModelMapException {
		if (tag == null) {
			throw new ModelMapException("null input");
		}
		String dicomTag = String.format("(0x%1$04X,0x%2$04X)", new Object[] {tag.getGroup(), tag.getElement()});

		return this.dicomTagToModelMap.get(dicomTag);
	}

	// no simple way to identify which of the muliple class that have the same attributes should be retrieved
	// this function would have to return multiple of the same indices mapped to different class, since the
	// dicom tag is hte same.
	@Deprecated
	public HashMap<Integer, String> getModelAttributeIndicesFromAttributeTag(AttributeTag tag) throws ModelMapException {
		HashMap<String, Integer> temp = this.getModelAttributeNamesFromAttributeTag(tag);

		HashMap<Integer, String> result = new HashMap<Integer, String>();

		String className = "somehting";
		Iterator iter = temp.keySet().iterator();
		while (iter.hasNext()) {
			String attributeName = String.class.cast(iter.next());

			if (attributeName.startsWith(className)) {


				result.put(temp.get(attributeName), attributeName);
			}
		}

		return result;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ModelMap map = null;
		try {
			map = new ModelMap();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ModelMapException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println(" model map retrieval mapping " + map.retrieveLevels.toString());
		System.out.println(" model map retrieval tag mapping " + map.retrieveLevelRequiredTags);

		try {
		System.out.println(" model map retrieval mapping patient " + map.getRetrieveLevel(InformationEntity.PATIENT));
		System.out.println(" model map retrieval mapping study " + map.getRetrieveLevel(InformationEntity.STUDY));
		System.out.println(" model map retrieval mapping series " + map.getRetrieveLevel(InformationEntity.SERIES));
		System.out.println(" model map retrieval mapping instance " + map.getRetrieveLevel(InformationEntity.INSTANCE));
		System.out.println(" model map retrieval mapping procedure step " + map.getRetrieveLevel(InformationEntity.PROCEDURESTEP));
		System.out.println(" model map retrieval mapping concatenation " + map.getRetrieveLevel(InformationEntity.CONCATENATION));
		System.out.println(" model map retrieval mapping frame " + map.getRetrieveLevel(InformationEntity.FRAME));

		System.out.println(" model map retrieval tag mapping patient " + map.getRetrieveLevelRequiredTag(PATIENT_STR).toString());
		System.out.println(" model map retrieval tag mapping study " + map.getRetrieveLevelRequiredTag(STUDY_STR).toString());
		System.out.println(" model map retrieval tag mapping series " + map.getRetrieveLevelRequiredTag(SERIES_STR).toString());
		System.out.println(" model map retrieval tag mapping image " + map.getRetrieveLevelRequiredTag(IMAGE_STR).toString());
		} catch (ModelMapException e) {
			e.printStackTrace();
		}
		Iterator iter = map.getRetrieveLevelIterator();
		System.out.println(" model map retrieval mapping iterator ");
		while (iter.hasNext()) {
			System.out.print("," + iter.next().toString());
		}
		System.out.println();

		try {
		System.out.println(" model map retrieval class mapping patient " + map.getModelClassFromQueryRetrieveLevel(PATIENT_STR));
		System.out.println(" model map retrieval class mapping study " + map.getModelClassFromQueryRetrieveLevel(STUDY_STR));
		System.out.println(" model map retrieval class mapping series " + map.getModelClassFromQueryRetrieveLevel(SERIES_STR));
		System.out.println(" model map retrieval class mapping image " + map.getModelClassFromQueryRetrieveLevel(IMAGE_STR));
		} catch (ModelMapException e) {
			e.printStackTrace();
		}
		ModelDictionary dict = map.getModelDict();
		DicomDictionary dicomdict = new DicomDictionary();

		System.out.println(" model dictionary = " + dict.toString());

		HashMap<String, Integer> names = null;
		try {
			names = map.getModelAttributeNamesFromAttributeTag(TagFromName.StudyInstanceUID);
		} catch (ModelMapException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(" study uid tag = " + TagFromName.StudyInstanceUID.toString());
		System.out.println(" from attribute tag study uid: " + names.toString());

		Collection<String> names2 = null;
		try {
			names2 = dict.getAttributeNameContains("Patient");
		} catch (ModelMapException e) {
			e.printStackTrace();
		}
		System.out.println(" attributes containing \"Patient\" " + names2.toString().replace(", ", "\n "));
		Iterator iter2 = names2.iterator();
		while (iter2.hasNext()) {
			String name = String.class.cast(iter2.next());
			AttributeTag t = null;
			try {
				t = map.getAttributeTagFromModelAttributeName(name);
			} catch (ModelMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (t != null) {
				System.out.println(" name = " + name + " dicom value " + t.toString() + " attribute tag name " + dicomdict.getNameFromTag(t));
			} else {
				System.out.println(" name = " + name + " not mapped to dicom ");
			}
		}

		names = null;
		try {
			names = map.getModelAttributeNamesFromAttributeTag(TagFromName.PixelSpacing);
		} catch (ModelMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" pixel spacing tag = " + TagFromName.PixelSpacing.toString());
		System.out.println(" from attribute tag pixel spacing: " + names.toString());

		names = null;
		try {
			names = map.getModelAttributeNamesFromAttributeTag(TagFromName.PatientAge);
		} catch (ModelMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" patient age tag = " + TagFromName.PatientAge.toString());
		System.out.println(" from attribute tag patient age: " + names.toString());

	}

	public DicomDictionary getDicomDict() {
		return dicomDict;
	}

	public ModelDictionary getModelDict() {
		return modelDict;
	}

}
