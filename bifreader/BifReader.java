package bifreader;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import aima.core.probability.bayes.BayesianNetwork;

/**
 * A utility class to read and write Bayesian networks in BIF format.
 *
 * http://www.heatonresearch.com/wiki/Bayesian_Interchange_Format
 */
public class BifReader {

    /**
     * Read a BIF file.
     *
     * @param f The BIF file.
     * @return The Bayesian network that was read.
     */
    public static BayesianNetwork readBIF(String f) {
        return readBIF(new File(f));
    }

    public static BayesianNetwork readBIF(File f) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(f);
            return readBIF(fis);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    // who cares at this point.
                }
            }
        }
    }

    /**
     * Read a BIF file from a stream.
     *
     * @param is The stream to read from.
     * @return The Bayesian network read.
     */
    public static BayesianNetwork readBIF(InputStream is) {
        try {
            MyBIFHandler h = new MyBIFHandler();
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(is, h);
            return h.getNetwork();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
