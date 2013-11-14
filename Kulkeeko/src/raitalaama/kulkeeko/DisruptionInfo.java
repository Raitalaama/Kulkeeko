package raitalaama.kulkeeko;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DisruptionInfo {
	
	
    private static final Map<String,String> LINETYPES;
    static {
    	Map<String,String> tmpMap = new HashMap<String,String>();
    		tmpMap.put("1","Helsingin sisäinen liikenne");
    		tmpMap.put("2","Raitiovaunu");
    		tmpMap.put("3","Espoon sisäinen liikenne");
    		tmpMap.put("4","Vantaan sisäinen liikenne");
    		tmpMap.put("5","Seutuliikenne");
    		tmpMap.put("6","Metro");
    		tmpMap.put("7","Lautta");
    		tmpMap.put("12","Juna (lähiliikenne)");
    		tmpMap.put("14","Kaikki julkinen liikenne");
    		tmpMap.put("36","Kirkkonummen sisäinen liikenne");
    		tmpMap.put("39","Keravan sisäinen liikenne");
    		LINETYPES = Collections.unmodifiableMap(tmpMap);
    }    		

	//TODO ajat mukaan

	// Which line info concerns
	private String lineName;
	private String joreId;
	
	private String linetype;
	// Explanation of the disruption
	private  String explanation;
	// Whether disruption affects only specific line or multiple
	private boolean specific;
	
	private boolean valid;
	// direction of the vehicle
	private int direction;
	
	DisruptionInfo(){
	}
	
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}


	
	

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String  getJoreId() {
		String id = "";
		if(isSpecific()){
			
			
			
			
		}
		
		return id;
	}

	public void setJoreId(String lineId) {
		this.joreId = lineId;
	}

	public String getLinetype() {
		return linetype;
	}

	public void setLinetype(String linetype) {
		this.linetype = linetype;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public boolean isSpecific() {
		return specific;
	}
	
	public void setSpecific(boolean specific){
		this.specific=specific;
	}
	
	public String toString(){
		String info = "";
		
		if(lineName!=null){
			info+=lineName;
		}
		if(explanation!=null){
			info+=" "+explanation;
		}
		if(linetype!=null){
			info+=" "+linetype;
		}
		
		
		return info;
	}


}
