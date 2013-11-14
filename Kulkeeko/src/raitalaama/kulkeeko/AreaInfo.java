package raitalaama.kulkeeko;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AreaInfo {

	/**
	 * 
	 * @param data JSONArray of public transportation stops in the area
	 */
	AreaInfo(JSONArray data) {
		mStops = new HashSet<Stop>();
		mAffectedLines = new ArrayList<String>();
		mAllDisruptions = new ArrayList<String>();

		for (int i = 0; i < data.length(); i++) {
			try {
				mStops.add(new Stop(data.getJSONObject(i)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private class Stop {

		Stop(JSONObject stop) throws JSONException {
			lines = new HashSet<String>();
			stopName = stop.getString("name");
			JSONObject stopDetails = stop.getJSONObject("details");
			JSONArray stopLines = stopDetails.getJSONArray("lines");
			for (int i = 0; i < stopLines.length(); i++) {
				String temppi = stopLines.getString(i);
				lines.add(temppi);
			}

		}

		public String stopName;

		public HashSet<String> getLines() {
			return lines;
		}

		/**
		 * @return JORE code of the line 
		 */
		public HashSet<String> getShortLines() {
			HashSet<String> shortLines = new HashSet<String>();
			for (String line : lines) {
				shortLines.add(line.substring(0, line.indexOf(':') - 1));
			}
			return shortLines;
		}

		private HashSet<String> lines;

		public String getStopName() {
			return stopName;
		}

	}

	private List<String> mAffectedLines;
	private List<String> mAllDisruptions;

	public List<String> getAffectedLines() {
		return mAffectedLines;
	}

	public List<String> getAllDisruptions() {
		return mAllDisruptions;
	}

	private HashSet<Stop> mStops;

	public HashSet<String> getLines() {
		HashSet<String> lines = new HashSet<String>();
		for (Stop stop : mStops) {
			lines.addAll(stop.getLines());
		}
		return lines;
	}

	/**
	 * As www.poikkeusinfo.fi and reittiopas.fi API use different coding for
	 * area of vehicle, this function checks if they are equal according to
	 * http://developer.reittiopas.fi/pages/en/http-get-interface-version-2.php
	 * @param area Area code from reittiopas API
	 * @param disruption Area code from poikkeusinfo.fi
	 * @return 
	 */
	private boolean isSameArea(String area, String disruption) {
		int areaCode = Integer.parseInt(area.substring(0, 1));
		int disruptionCode = Integer.parseInt(disruption);
		switch (areaCode) {
		case 1:
			if (disruptionCode == (1 | 2 | 6 | 7 | 14)) {
				return true;
			}
			return false;
		case 2:
			if (disruptionCode == (3 | 5 | 23 | 25 | 14)) {
				return true;
			}
			return false;
		case 3:
			if (disruptionCode == (12 | 14)) {
				return true;
			}
			return false;
		case 4:
			if (disruptionCode == (4 | 5 | 24 | 25 | 14)) {
				return true;
			}
			return false;
		case 5:
			if (disruptionCode == (5 | 25 | 14)) {
				return true;
			}
			return false;
		case 7:
			if (disruptionCode == (8 | 14)) {
				return true;
			}
			return false;

		}
		return false;
	}

	private String joreToExplanation(String input) {
		String lineName = joreToLineName(input);
		String destination = input.substring(input.indexOf(":") + 1);
		return lineName + " Suuntana " + destination; // TODO kayta
														// kielivapaata settia
	}

	private String joreToLineName(String input) {
		String tmp = input.substring(1);
		while (tmp.startsWith("0")) {
			tmp = tmp.substring(1);
		}
		return tmp.substring(0, tmp.indexOf(":") - 1);
	}

	private String joreToDirection(String input) {
		return input.substring(input.indexOf(":") - 1, input.indexOf(":"));
	}

	public void filterWithDisruptions(List<DisruptionInfo> disruptions) {
		listDisruptions(disruptions);
		HashSet<String> lines = getLines();
		for (String line : lines) {
			for (DisruptionInfo disruption : disruptions) {
				if (disruption.isValid()) {
					if (disruption.isSpecific()) {
						if (!isSameArea(line, disruption.getLinetype())) {
							continue;
						}
						if (!joreToLineName(line).equals(
								disruption.getLineName())) {
							continue;
						}
						if (!joreToDirection(line).equals(
								disruption.getDirection())) {
							continue;
						}
						mAffectedLines.add(joreToExplanation(line));

					} else {
						if (isSameArea(line, disruption.getLinetype()))
							mAffectedLines.add(joreToExplanation(line));
					}
				}
			}
		}
	}

	private void listDisruptions(List<DisruptionInfo> disruptions) {
		for (DisruptionInfo disruption : disruptions) {
			if (disruption.isValid()) {
				mAllDisruptions.add(disruption.getExplanation());
			}
		}

	}

	public List<String> getStops() {
		List<String> stops = new ArrayList<String>();
		for (Stop stop : mStops) {
			stops.add(stop.getStopName());
		}

		return stops;
	}

	public List<String> getLineExplanations() {
		List<String> lineExplanations = new ArrayList<String>();
		HashSet<String> lines = getLines();
		for (String line : lines) {
			lineExplanations.add(joreToExplanation(line));
		}
		return lineExplanations;
	}

	public HashSet<String> getShortLines() {
		HashSet<String> shortLines = new HashSet<String>();
		for (Stop stop : mStops) {
			shortLines.addAll(stop.getShortLines());
		}
		return shortLines;
	}

}
