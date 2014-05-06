package gov.nih.nci.ivi.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import edu.osu.bmi.utils.io.zip.ZipEntryOutputStream;


public class Zipper {

	static final int BUFFER = 8192;


	/**
	   inputFiles must be a list of valid filenames, with no directories.
	 */
	public static void zip(String outputFilename, String[] inputFiles,
			boolean compressed) throws FileNotFoundException, IOException {
		if (inputFiles.length == 0)
			return;
		BufferedInputStream origin = null;
		FileOutputStream dest = new FileOutputStream(outputFilename);

		CheckedOutputStream checksum = new CheckedOutputStream(dest,
				new Adler32());
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				checksum));
		if (compressed)
			out.setMethod(ZipOutputStream.DEFLATED);
		else
			out.setMethod(ZipOutputStream.STORED);
		byte data[] = new byte[BUFFER];
		CRC32 checksumEngine = new CRC32();
		int count;
		//System.out.println("zipping " + inputFiles.length + " files with "
		//		+ (compressed ? "compression" : "no compression"));
		double t1 = System.currentTimeMillis() / 1000.0;
		for (int i = 0; i < inputFiles.length; i++) {
			// System.out.println("Adding: "+inputFiles[i]);
			origin = new BufferedInputStream(
					new FileInputStream(inputFiles[i]), BUFFER);
			ZipEntry entry = new ZipEntry(new File(inputFiles[i]).getName());
			if (!compressed) {
				checksumEngine.reset();
				while ((count = origin.read(data, 0, BUFFER)) != -1)
					checksumEngine.update(data, 0, count);
				origin.close();
				entry.setCrc(checksumEngine.getValue());
				entry.setSize(new File(inputFiles[i]).length());
				entry.setCompressedSize(new File(inputFiles[i]).length());
			}
			out.putNextEntry(entry);
			if (!compressed)
				origin = new BufferedInputStream(new FileInputStream(
						inputFiles[i]), BUFFER);
			while ((count = origin.read(data, 0, BUFFER)) != -1)
				out.write(data, 0, count);
			origin.close();
		}
		out.close();
		double t2 = System.currentTimeMillis() / 1000.0;
		//System.out.println("zipping done in " + (t2 - t1) + " seconds");
	}


	public static void newzip(String outputFilename, String[] inputFiles,
			boolean compressed) throws FileNotFoundException, IOException {
		if (inputFiles.length == 0)
			return;
		InputStream origin = null;
		FileOutputStream dest = new FileOutputStream(outputFilename);

		ZipOutputStream pos = new ZipOutputStream(new BufferedOutputStream(dest));
		//ZipEntryOutputStream zeos = null;
		//System.out.println("zipping " + inputFiles.length + " files with "
		//		+ (compressed ? "compression" : "no compression"));
		int method = ZipEntry.STORED;
		if (compressed) method = ZipEntry.DEFLATED;
		byte[] data = new byte[BUFFER];
		int bytesRead = 0;
		BufferedOutputStream bos = null;
		double t1 = System.currentTimeMillis() / 1000.0;
		for (int i = 0; i < inputFiles.length; i++) {
			// System.out.println("Adding: "+inputFiles[i]);
			//origin = new BufferedInputStream(
			//		new FileInputStream(inputFiles[i]), BUFFER);
			origin = new FileInputStream(inputFiles[i]);

			int filesize = (int)new File(inputFiles[i]).length();

			ZipEntryOutputStream zeos = new ZipEntryOutputStream(pos, new File(inputFiles[i]).getName(), method);
			//bos = new BufferedOutputStream(zeos);



			while ((bytesRead = origin.read(data, 0, BUFFER)) > 0) {
				//System.out.println(" read " + bytesRead );
				zeos.write(data, 0, bytesRead);
			}
			zeos.close();

		}
		pos.close();
		double t2 = System.currentTimeMillis() / 1000.0;
		//System.out.println("zipping done in " + (t2 - t1) + " seconds");
	}

	/**
	inputFiles must be a list of valid filenames, with no directories.
	 */
	public static void zipString(String outputFilename, String entryName,
			String inputString, boolean compressed)
			throws FileNotFoundException, IOException {
		zipStrings(outputFilename, new String[] { entryName },
				new String[] { inputString }, compressed);
	}

	public static void zipStrings(String outputFilename, String[] entries,
			String[] inputStrings, boolean compressed)
			throws FileNotFoundException, IOException {
		if (!(entries.length == inputStrings.length)) {
			throw new IllegalArgumentException("entries array length must match inputStrings array length");
		}

		FileOutputStream dest = new FileOutputStream(outputFilename);
		//need this stream for "store-only" capability (no compression)
		CheckedOutputStream checksum = new CheckedOutputStream(dest,
				new Adler32());
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				checksum));
		if (compressed)
			out.setMethod(ZipOutputStream.DEFLATED);
		else
			out.setMethod(ZipOutputStream.STORED);
		byte data[] = new byte[BUFFER];
		CRC32 checksumEngine = new CRC32();
		int count;
		double t1 = System.currentTimeMillis() / 1000.0;

		for (int i = 0; i < inputStrings.length; i++) {
			byte[] bytes = inputStrings[i].getBytes();
			ByteArrayInputStream biStream = new ByteArrayInputStream(bytes);
			BufferedInputStream bStream = new BufferedInputStream(biStream);
			ZipEntry entry = new ZipEntry(entries[i]);
			if (!compressed) {
				checksumEngine.reset();
				while ((count = bStream.read(data, 0, BUFFER)) != -1)
					checksumEngine.update(data, 0, count);
				bStream.close();
				entry.setCrc(checksumEngine.getValue());
				entry.setSize(bytes.length);
				entry.setCompressedSize(bytes.length);
			}
			out.putNextEntry(entry);
			if (!compressed) {
				biStream = new ByteArrayInputStream(bytes);
				bStream = new BufferedInputStream(biStream);
			}
			while ((count = bStream.read(data, 0, BUFFER)) != -1)
				out.write(data, 0, count);
			bStream.close();
		}
		out.close();
		double t2 = System.currentTimeMillis() / 1000.0;
		System.out.println("zipping done in " + (t2 - t1) + " seconds");
	}

	public static String[] unzip(String inputFilename, String outputDirectory)
			throws FileNotFoundException, IOException {
		final int BUFFER = 32768;
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(inputFilename);
		CheckedInputStream checksum = new CheckedInputStream(fis, new Adler32());
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
				checksum));
		ZipEntry entry;
		Vector<String> out = new java.util.Vector<String>();
		while ((entry = zis.getNextEntry()) != null) {
			// System.out.println("Extracting: " +entry);
			int count;
			byte data[] = new byte[BUFFER];
			// write the files to the disk
			String name = outputDirectory + File.separator
					+ new File(entry.getName()).getName();
			FileOutputStream fos = new FileOutputStream(name);
			dest = new BufferedOutputStream(fos, BUFFER);
			while ((count = zis.read(data, 0, BUFFER)) != -1)
				dest.write(data, 0, count);
			dest.flush();
			dest.close();
			out.add(name);
		}
		zis.close();
		String[] out2 = new String[out.size()];
		out2 = out.toArray(out2);
		return out2;
	}


	public static String[] newunzip(String inputFilename, String outputDirectory)
		throws FileNotFoundException, IOException {
		final int BUFFER = 32768;
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(inputFilename);

		ZipInputStream pis = new ZipInputStream(new BufferedInputStream(fis));

		Vector<String> out = new Vector<String>();
		byte[] data;
		String name = null;
		ZipEntryInputStream zeis = null;
		int bytesRead = 0;
		int totalRead = 0;
		while (true) {
			try {
				zeis = new ZipEntryInputStream(pis);
			} catch (EOFException e) {
				// done with reading the whole thing
				break;
			}


			data = new byte[BUFFER];

			BufferedInputStream bis = new BufferedInputStream(zeis);
			totalRead = 0;
			//System.out.println("Extracting: " + name.toString());
			// write the files to the disk
			name = zeis.getName();

			String fname = outputDirectory + File.separator + (new File(name)).getName();
			FileOutputStream fos = new FileOutputStream(fname);
			dest = new BufferedOutputStream(fos, BUFFER);

			while ((bytesRead = bis.read(data, 0, data.length)) > 0) {
				dest.write(data, 0, bytesRead);
				//System.out.println("read " + bytesRead);
				totalRead += bytesRead;
			}
			bis.close();
			dest.flush();
			dest.close();
			out.add(fname);
		}
		pis.close();
		String[] out2 = new String[out.size()];
		out2 = out.toArray(out2);
		return out2;
	}

	public static void usage(String appname) {
		System.out.println("usage: " + appname + "");
		System.exit(1);
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 0)
			usage("Zipper");
		String tempdir;
		{
			int rep = 0;
			while (true) {
				tempdir = System.getProperty("java.io.tmpdir")
						+ ".tmp." + rep;
				if (new File(tempdir).mkdirs())
					break;
				rep += 1;
			}
		}

		int i;
		int reps = 60;
		String[] inputs = new String[reps];
		byte[] buf = new byte[524288];
		for (i = 0; i < reps; i++) {
			for (int j = 0; j < 524288; j++) {
				buf[j] = (byte)Math.rint(Math.random() * (Byte.MAX_VALUE-1));
			}

			String fn = tempdir + File.separator + i;
			FileOutputStream f = new FileOutputStream(fn);
			f.write(buf);
			f.close();
			inputs[i] = fn;
		}

		String tempfn = File.createTempFile("tmp", "").getCanonicalPath();
		String tempfn2 = File.createTempFile("tmp", "").getCanonicalPath();

		Zipper.zip(tempfn, inputs, true);
		Zipper.zip(tempfn2, inputs, false);

		double t1 = System.currentTimeMillis() / 1000.0;
		Zipper.zip(tempfn, inputs, true);
		double t2 = System.currentTimeMillis() / 1000.0;
		System.out.println("zip elapsed compressed was " + (t2 - t1) + " size is " + new File(tempfn).length());
		double t3 = System.currentTimeMillis() / 1000.0;
		Zipper.zip(tempfn2, inputs, false);
		double t4 = System.currentTimeMillis() / 1000.0;
		System.out.println("zip elapsed uncompressed was " + (t4 - t3)+ " size is " + new File(tempfn2).length());


		t1 = System.currentTimeMillis() / 1000.0;
		Zipper.newzip(tempfn, inputs, true);
		t2 = System.currentTimeMillis() / 1000.0;
		System.out.println("newzip2 elapsed compressed was " + (t2 - t1) + " size is " + new File(tempfn).length());
		t3 = System.currentTimeMillis() / 1000.0;
		Zipper.newzip(tempfn2, inputs, false);
		t4 = System.currentTimeMillis() / 1000.0;
		System.out.println("newzip2 elapsed uncompressed was " + (t4 - t3)+ " size is " + new File(tempfn2).length());

		String tempdir2;
		String tempdir3;
		{
			int rep = 0;
			while (true) {
				tempdir2 = System.getProperty("java.io.tmpdir")
						+ File.separator + ".tmp." + rep;
				//System.out.println(tempdir2);
				if (new File(tempdir2).mkdirs())
					break;
				rep += 1;
			}
			while (true) {
				tempdir3 = System.getProperty("java.io.tmpdir")
						+ File.separator + ".tmp." + rep;
				//System.out.println(tempdir3);
				if (new File(tempdir3).mkdirs())
					break;
				rep += 1;
			}
		}

		System.out.println("tempfn: " + tempfn);
		System.out.println("tempdir: " + tempdir);

		System.out.println("tempdir2: " + tempdir2);
		System.out.println("tempdir3: " + tempdir3);

		String[] extracted = Zipper.unzip(tempfn, tempdir2);
		extracted = Zipper.unzip(tempfn2, tempdir3);
		IOUtils.deleteDirectory(tempdir2);
		new File(tempdir2).mkdirs();
		IOUtils.deleteDirectory(tempdir3);
		new File(tempdir3).mkdirs();


		double t5 = System.currentTimeMillis() / 1000.0;
		extracted = Zipper.unzip(tempfn, tempdir2);
		double t6 = System.currentTimeMillis() / 1000.0;
		System.out.println("unzip compressed elapsed  was " + (t6 - t5));
		double t7 = System.currentTimeMillis() / 1000.0;
		extracted = Zipper.unzip(tempfn2, tempdir3);
		double t8 = System.currentTimeMillis() / 1000.0;
		System.out.println("unzip uncompressed elapsed  was " + (t8 - t7));

		IOUtils.deleteDirectory(tempdir2);
		new File(tempdir2).mkdirs();
		IOUtils.deleteDirectory(tempdir3);
		new File(tempdir3).mkdirs();


		IOUtils.deleteDirectory(tempdir2);
		new File(tempdir2).mkdirs();
		IOUtils.deleteDirectory(tempdir3);
		new File(tempdir3).mkdirs();

		t5 = System.currentTimeMillis() / 1000.0;
		extracted = Zipper.newunzip(tempfn, tempdir2);
		t6 = System.currentTimeMillis() / 1000.0;
		System.out.println("newunzip2 compressed elapsed  was " + (t6 - t5));
		t7 = System.currentTimeMillis() / 1000.0;
		extracted = Zipper.newunzip(tempfn2, tempdir3);
		t8 = System.currentTimeMillis() / 1000.0;
		System.out.println("newunzip2 uncompressed elapsed  was " + (t8 - t7));


		IOUtils.deleteDirectory(tempdir2);
		IOUtils.deleteDirectory(tempdir3);


		//         new File(tempfn).delete();
		IOUtils.deleteDirectory(tempdir);


	}

}
