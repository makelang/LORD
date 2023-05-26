package com.kleegroup.lord.moteur.reader;

import java.io.*;


import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.log4j.Logger;

import com.kleegroup.lord.moteur.exceptions.CaractereInterdit;
import com.kleegroup.lord.moteur.util.CountingReader;
import com.kleegroup.lord.moteur.util.ICSVDataSource;

/**
 * Cette classe sert comme adaptateur pour la classe CsvReader.
 * Elle permet au moteur de l'utiliser comme source de donnée.
 */
public class CsvReaderAdapter implements ICSVDataSource {
	private static final org.apache.log4j.Logger logAppli = Logger.getLogger(CsvReaderAdapter.class);

	protected CSVReader reader;
	protected CountingReader counter;
	protected long size=0;
	
	/**
	 * @param path path le chemin d'acces du fichier a lire
	 * @param encoding l'encodage du fichier à lire
	 */
	public CsvReaderAdapter(String path, String encoding, char separator, int nbSkipLines){
		try{
			size=(new File(path)).length();
			counter=new CountingReader(new InputStreamReader(new FileInputStream(path), encoding));
		}catch(final UnsupportedEncodingException | FileNotFoundException e){
			counter=new CountingReader(Reader.nullReader());
			logAppli.error(e);
		}
		CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
		reader=new CSVReaderBuilder(counter)
				.withCSVParser(parser)
				.withSkipLines(nbSkipLines)
				.build();
	}


	/** {@inheritDoc}
	 * @throws CaractereInterdit
	 */
	@Override
	public String[] next() throws IOException, CaractereInterdit {
		try {
			return reader.readNext();
		} catch (final CsvValidationException e) {
			throw new CaractereInterdit(e.getLineNumber(), 0, "");
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public int getPosition() {
		return (int)reader.getLinesRead();
	}
	
	/** {@inheritDoc} */
	@Override
	public long getTotalSize() {
		return size;
	}
	
	/** {@inheritDoc} */
	@Override
	public long getNbCharactersRead(){
		return counter.getNbCharactersRead();
	}
	
}
