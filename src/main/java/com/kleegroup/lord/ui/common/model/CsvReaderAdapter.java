package com.kleegroup.lord.ui.common.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kleegroup.lord.moteur.ICSVDataSource;
import com.kleegroup.lord.moteur.exceptions.CaractereInterdit;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

/**
 * Cette classe sert comme adaptateur pour la classe CsvReader.
 * Elle permet au moteur de l'utiliser comme source de donnée.
 */
public class CsvReaderAdapter implements ICSVDataSource {
	private static final Log logAppli = LogFactory.getLog(CsvReaderAdapter.class);

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
