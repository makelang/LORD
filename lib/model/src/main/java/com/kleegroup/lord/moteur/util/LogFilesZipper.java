package com.kleegroup.lord.moteur.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 *Zip les fichier de log dauns un fichier désigné.
 *
 *voir < a href=http://www.java-tips.org/java-se-tips/java.util.zip/how-to-create-a-zip-file.html>
 *source du code </a> 
 *
 */
public class LogFilesZipper {
	private static org.apache.log4j.Logger logAppli = Logger.getLogger(LogFilesZipper.class);

	/**
	 * @param outputZip le File qui représente le fichier de zip
	 * @param filenames les fichiers à zipper
	 * @throws IOException si une erreur de lecture a lieu
	 */
	public static void zip(File outputZip, List<String> filenames) throws IOException {
		// Create the ZIP file
		List<String> fileList = getValidFiles(filenames);
		if (fileList.isEmpty()) {
			return;
		}
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputZip))){

			// Compress the files
			for (String filename : fileList) {
				File f = new File(filename);

				try (FileInputStream in = new FileInputStream(filename)) {
					// Add ZIP entry to output stream.
					out.putNextEntry(new ZipEntry(f.getName()));

					// Transfer bytes from the file to the ZIP file
					in.transferTo(out);

					// Complete the entry
					out.closeEntry();
				} catch (IOException ex) {
					logAppli.error(ex);
				}
			}
		} catch (IOException ex) {
			logAppli.error(ex);
		}
	}

	private static List<String> getValidFiles(List<String> filenames) {
		return filenames.stream()
				.map(File::new)
				.filter(LogFilesZipper::canReadFile)
				.map(File::getAbsolutePath)
				.collect(Collectors.toList());
	}

	private static boolean canReadFile(File aTester) {
		return aTester.exists() && aTester.canRead();
	}
}
