/**
 * 
 */
package raitalaama.kulkeeko;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

/**
 * @author Raitalaama
 *
 */

public class DisruptionParser {
	
    private static final String ns = null;
    
		

    public List<DisruptionInfo> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(in,"ISO-8859-1");
            parser.nextTag();
            Log.d("encoding " ,parser.getInputEncoding());


            return readDisruptions(parser);
        } finally {
            in.close();
        }
    }

    private List<DisruptionInfo> readDisruptions(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<DisruptionInfo> disruptions = new ArrayList<DisruptionInfo>();

        parser.require(XmlPullParser.START_TAG, ns, "DISRUPTIONS");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("DISRUPTION")) {
            
    				disruptions.add(readDisruption(parser));
    		} else {
                skip(parser);
            }
        }
        return disruptions;
    }
    

    private DisruptionInfo readDisruption(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "DISRUPTION");
        DisruptionInfo mDisruption = new DisruptionInfo();
        if(parser.getAttributeValue(null, "type").equals("2")){
        	mDisruption.setSpecific(true);
        }
        else{
        	mDisruption.setSpecific(false);
        }
        	
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("VALIDITY")) {
                readValidity(parser,mDisruption);
            } else if (name.equals("INFO")) {
                readInfo(parser,mDisruption);
            } else if (name.equals("TARGETS")) {
                readTargets(parser,mDisruption);
            } else {
                skip(parser);
            }
        }
        return mDisruption;
    }
    
    private void readTargets(XmlPullParser parser, DisruptionInfo disruption) throws XmlPullParserException, IOException {
    	 parser.require(XmlPullParser.START_TAG, ns, "TARGETS");
         while (parser.next() != XmlPullParser.END_TAG) {
             if (parser.getEventType() != XmlPullParser.START_TAG) {
                 continue;
             }
             String name = parser.getName();
             if (name.equals("LINE")) {
                 readLine(parser,disruption);
             } else if (name.equals("LINETYPE")) {
                 readLinetype(parser,disruption);
             }  else {
                 skip(parser);
             }
         }		
	}

    

	private void readLinetype(XmlPullParser parser, DisruptionInfo disruption) throws XmlPullParserException, IOException {
		   parser.require(XmlPullParser.START_TAG, ns, "LINETYPE");
	       String linetype = parser.getAttributeValue(null, "id");
	       disruption.setLinetype(linetype);
	       parser.nextTag();
	       parser.require(XmlPullParser.END_TAG, ns, "VALIDITY");
	}

	private void readLine(XmlPullParser parser, DisruptionInfo disruption) throws XmlPullParserException, IOException {
		   parser.require(XmlPullParser.START_TAG, ns, "LINE");
	       disruption.setJoreId(parser.getAttributeValue(null, "id"));
	       disruption.setDirection(Integer.parseInt(parser.getAttributeValue(null, "direction")));
	       disruption.setLinetype(parser.getAttributeValue(null, "linetype"));
	       disruption.setLineName(readText(parser));
	       parser.require(XmlPullParser.END_TAG, ns, "LINE");

	     
	}

	private void readInfo(XmlPullParser parser, DisruptionInfo disruption) throws XmlPullParserException, IOException {
        parser.nextTag();
		disruption.setExplanation(readText(parser));
        parser.nextTag();

		
	}

	private void readValidity(XmlPullParser parser,DisruptionInfo disruption)throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "VALIDITY");
        String validity = parser.getAttributeValue(null, "status");
        disruption.setValid(validity.contentEquals("1"));
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "VALIDITY");
    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";

        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
