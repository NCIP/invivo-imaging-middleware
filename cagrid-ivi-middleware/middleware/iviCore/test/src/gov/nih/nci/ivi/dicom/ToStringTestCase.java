package gov.nih.nci.ivi.dicom;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.axis.types.Time;

import junit.framework.TestCase;

public class ToStringTestCase extends TestCase {

	public ToStringTestCase(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testToDICOMString() {
		Calendar cal = Calendar.getInstance();
		System.out.println(cal.getClass().getCanonicalName());
		System.out.println(cal.toString());
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyyMMddHHmmss.SSS'000'Z");
		System.out.println(dateTimeFormatter.format(cal.getTime()));

		Date d = new Date();
		System.out.println(d.getClass().getCanonicalName());
		System.out.println(d.toString());
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		System.out.println(dateFormatter.format(d));

		Time t = new Time(cal);
		System.out.println(t.getClass().getCanonicalName());
		System.out.println(t.toString());
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HHmmss.SSS'000'");
		System.out.println(timeFormatter.format(t.getAsCalendar().getTime()));

	}

}
