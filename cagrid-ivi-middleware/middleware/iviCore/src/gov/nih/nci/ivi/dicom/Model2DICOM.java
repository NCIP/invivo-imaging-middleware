package gov.nih.nci.ivi.dicom;

import gov.nih.nci.ivi.dicom.modelmap.ModelDictionary;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.ValueRepresentation;

public class Model2DICOM {
	ModelMap mMap = null;
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyyMMdd");
	private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
			"yyyyMMddHHmmss.SSS'000'Z");
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat(
			"HHmmss.SSS'000'");

	public Model2DICOM(ModelMap map) {
		mMap = map;
	}

	public AttributeList model2DICOM(Object obj, AttributeList inputList) {

		AttributeList result = inputList;
		Class resultClass = obj.getClass();

		//System.out.println("Class name is " + resultClass.getCanonicalName());

		ModelDictionary mdict = mMap.getModelDict();

		// first get the attribute values
		result = modelClass2DICOM(obj, result);

		// next go through the children of the class. Again, the links should
		// not be bidirectional
		// children include the hierarchical parent
		Iterator childIter = null;
		try {
			childIter = mdict.getChildFieldsFromClass(resultClass).iterator();
		} catch (ModelMapException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (childIter != null && childIter.hasNext()) {
			Field childField = Field.class.cast(childIter.next());
			Method getter = null;
			try {
				getter = mdict.getGettersFromField(childField).get(0);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (ModelMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Object child = null;
			try {
				child = getter.invoke(obj, new Object[] {});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			if (child != null) {
				result = model2DICOM(child, result);
			}
		}

		// attribute.addValue(patient.getPT_NAME() != null ?
		// patient.getPT_NAME(): "Annonymized");
		// attribute.addValue(patient.getPT_ID() != null ? patient.getPT_ID():
		// "Annonymized");

		return result;

	}

	private AttributeList modelClass2DICOM(Object obj, AttributeList inputList) {
		AttributeList result = inputList;

		ModelDictionary mdict = mMap.getModelDict();

		Class ie = obj.getClass();
		HashSet<String> attrNames = null;
		try {
			attrNames = mdict.getAttributeNamesFromInformationEntityClass(ie);
		} catch (ModelMapException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Iterator<String> iter = attrNames.iterator();

		String attrName = null;
		int attrId = ModelMap.NON_ARRAY_INDEX;
		while (iter.hasNext()) {
			attrName = iter.next();

			// first find the tag
			AttributeTag tag = null;
			try {
				tag = mMap.getAttributeTagFromModelAttributeName(attrName);
			} catch (ModelMapException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// next find the attribute value's index id
			attrId = ModelMap.NON_ARRAY_INDEX;
			try {
				attrId = mMap.getArrayIndexFromModelAttributeName(attrName);
			} catch (ModelMapException e1) {
				System.out.println("WARNING: " + e1.getMessage());
				;
			}

			if (tag == null || tag.isPrivate()) {
				// ignore private tag.
				continue;
			}

			// then find out about the VR
			byte[] vr = mMap.getDicomDict().getValueRepresentationFromTag(tag);
			Attribute attr = result.get(tag);
			if (attr == null) {
				try {
					attr = AttributeFactory.newAttribute(tag, vr);
				} catch (DicomException e) {
					e.printStackTrace();
				}
			}

			// find the right getter and get the value
			Vector<Method> getters = null;
			try {
				getters = mdict.getGettersFromAttributeName(attrName);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ModelMapException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			Method getter = getters.get(0);
			Object value = null;
			try {
				// System.out.println("getting value for " + attrName);
				value = getter.invoke(obj, new Object[] {});
				// System.out.println(" value is " + value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			if (value != null) {
				// first convert the non java lang types. these are
				// java.util.Date, org.apache.axis.types.Time, and
				// java.util.Calendar
				// convert these to Date
				if (value instanceof org.apache.axis.types.Time) {
					value = ((org.apache.axis.types.Time) value)
							.getAsCalendar().getTime();
				} else if (value instanceof java.util.Calendar) {
					value = ((java.util.Calendar) value).getTime();
				}

				// convert the value to string
				// standard java object types, so can probably rely on the
				// toString method
				// this may be problematic.. - what if the attribute does not
				// take String? or requires native data type?
				String valStr = value.toString();
				if (value instanceof java.util.Date) {
					if (ValueRepresentation.isDateVR(vr)) {
						valStr = dateFormatter.format((java.util.Date) value);
					} else if (ValueRepresentation.isTimeVR(vr)) {
						valStr = timeFormatter.format((java.util.Date) value);
					} else if (ValueRepresentation.isDateTimeVR(vr)) {
						valStr = dateTimeFormatter
								.format((java.util.Date) value);
					}
				}

				if (attrId == ModelMap.NON_ARRAY_INDEX) {
					// not an array, so just set the value
					try {
						attr.addValue(valStr);

					} catch (DicomException e) {
						e.printStackTrace();
					}
				} else {
					// have to create a vector. There may not be any values in
					// attr at first.

					// need to create a new vector, since we may be inserting in
					// the middle somewhere or add to the end.
					Vector<String> newValues = new Vector<String>(1);
					try {
						String[] values = attr.getStringValues();
						if (values != null) {
							Collections.addAll(newValues, values);
						}
					} catch (DicomException e) {
						e.printStackTrace();
					}

					if (attrId >= newValues.size())
						newValues.setSize(attrId + 1);
					System.out.println("size = " + newValues.size());
					System.out.println("attrId = " + attrId);
					newValues.set(attrId, valStr);

					try {
						attr.removeValues();
						// TCP untested: reset and repopulate the attribute
						// attr = AttributeFactory.newAttribute(tag,vr);
					} catch (DicomException e) {
						e.printStackTrace();
					}

					for (int i = 0; i < newValues.size(); i++) {
						try {
							attr.addValue(newValues.get(i));
						} catch (DicomException e) {
							e.printStackTrace();
						}
					}
				}
				result.put(attr);
			}
		}

		return result;
	}
}