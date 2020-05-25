metadata {
	definition (name: "Virtual Thermostat Device",
    namespace: "piratemedia/smartthings",
    author: "Eliot S. + Steffen N.",
    mnmn: "SmartThings", 
    vid: "generic-thermostat-1",
    executeCommandsLocally: true,
    ocfDeviceType: "oic.d.thermostat") {
		capability "Temperature Measurement"
		capability "Thermostat"
		capability "Thermostat Mode"
		capability "Thermostat Heating Setpoint"
		capability "Thermostat Cooling Setpoint"
		capability "Thermostat Operating State"
		capability "Configuration"
		capability "Refresh"

		command "refresh"
		command "poll"
        
		command "offbtn"
		command "coolbtn"
		command "heatbtn"
		command "autobtn"
		command "levelUpDown"
		command "levelUp"
		command "levelDown"
		command "heatingSetpointUp"
		command "heatingSetpointDown"
		command "coolingSetpointUp"
		command "coolingSetpointDown"
		command "changeMode"
		command "setVirtualTemperature", ["number"]
		command "setHeatingStatus", ["string"]
        
		attribute "temperatureUnit", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"temperature", type:"thermostat", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("default", label:'${currentValue}°', unit: unitString())
			}
            
			tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
				attributeState("VALUE_UP", action: "levelUp")
				attributeState("VALUE_DOWN", action: "levelDown")
			}
            
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {iuj
				// valid values are thermostatOperatingState — ["heating", "idle", "pending cool", "vent economizer", "cooling", "pending heat", "fan only"]
				// https://graph.api.smartthings.com/ide/doc/capabilities
				attributeState("idle",		    backgroundColor: "#44B621")
				attributeState("heating",	    backgroundColor: "#d4852a")
				attributeState("cooling",	    backgroundColor: "#2a8ad4")
				attributeState("pending heat",	backgroundColor: "#ffd19c")
				attributeState("pending cool",	backgroundColor: "#85b3d6")
			}
            
			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("cool", label:'Cool')
				attributeState("heat", label:'Heat')
				attributeState("auto", label:'Auto')
				attributeState("off", label:'Off')
			}
            
			tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
				attributeState("default", label:'${currentValue}')
			}

			tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
				attributeState("default", label:'${currentValue}')
			}
		}
        
		valueTile("temp2", "device.temperature", width: 2, height: 2, decoration: "flat") {
			state("default", label:'${currentValue}°', icon:"https://raw.githubusercontent.com/eliotstocker/SmartThings-VirtualThermostat-WithDTH/master/device.png",
					backgroundColors: getTempColors(), canChangeIcon: true)
		}
        
		standardTile("thermostatMode", "device.thermostatMode", width:2, height:1, decoration: "flat") {
			state("off", 	action:"changeMode", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/off_icon.png")
			state("heat", 	action:"changeMode", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_heat.png")
			state("cool", 	action:"changeMode", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_cool.png")
			state("auto", 	action:"changeMode", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_auto.png")
			state("updating", label:"", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png")
		}
        
		standardTile("offBtn", "device.off", width:1, height:1, decoration: "flat") {
			state("Off", action: "offbtn", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/off_icon.png")
		}
        
		standardTile("heatBtn", "device.canHeat", width:1, height:1, decoration: "flat") {
			state("Heat", action: "heatbtn", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_heat.png")
			state "false", label: ''
		}

		standardTile("coolBtn", "device.canCool", width:1, height:1, decoration: "flat") {
			state("Cool", action: "coolbtn", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_cool.png")
			state "false", label: ''
		}

		standardTile("autoBtn", "device.canAuto", width:1, height:1, decoration: "flat") {
			state("Auto", action: "autobtn", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_auto.png")
			state "false", label: ''
		}

		standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
			state "Refresh", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
		valueTile("heatingSetpoint", "device.thermostatSetpoint", width: 1, height: 1) {
			state("heatingSetpoint", label:'${currentValue}', unit: unitString(), foregroundColor: "#FFFFFF",
				backgroundColors: [ [value: 0, color: "#FFFFFF"], [value: 7, color: "#FF3300"], [value: 15, color: "#FF3300"] ])
			state("disabled", label: '', foregroundColor: "#FFFFFF", backgroundColor: "#FFFFFF")
		}
        
		standardTile("heatingSetpointUp", "device.thermostatSetpoint", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label: '', action:"heatingSetpointUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_up.png"
			state "", label: ''
		}
        
		standardTile("heatingSetpointDown", "device.thermostatSetpoint",  width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label:'', action:"heatingSetpointDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_down.png"
			state "", label: ''
		}
        
		controlTile("heatSliderControl", "device.thermostatSetpoint", "slider", height: 1, width: 3, range: getRange(), inactiveLabel: false) {
			state "default", action:"setHeatingSetpoint", backgroundColor:"#FF3300"
			state "", label: ''
		}

		valueTile("coolingSetpoint", "device.thermostatSetpoint", width: 1, height: 1) {
			state("coolingSetpoint", label:'${currentValue}', unit: unitString(), foregroundColor: "#FFFFFF",
					backgroundColors: [ [value: 0, color: "#FFFFFF"], [value: 7, color: "#0022FF"], [value: 15, color: "#0022FF"] ])
			state("disabled", label: '', foregroundColor: "#FFFFFF", backgroundColor: "#FFFFFF")
		}

		standardTile("coolingSetpointUp", "device.thermostatSetpoint", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label: '', action:"coolingSetpointUp", icon:"https://raw.githubusercontent.com/racarmichael/SmartThings-VirtualThermostat-WithDTH/master/images/cool_arrow_up.png"
			state "", label: ''
		}

		standardTile("coolingSetpointDown", "device.thermostatSetpoint",  width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label:'', action:"coolingSetpointDown", icon:"https://raw.githubusercontent.com/racarmichael/SmartThings-VirtualThermostat-WithDTH/master/images/cool_arrow_down.png"
			state "", label: ''
		}

		controlTile("coolSliderControl", "device.thermostatSetpoint", "slider", height: 1, width: 4, range: getRange(), inactiveLabel: false) {
			state "default", action:"setCoolingSetpoint", backgroundColor:"#0022ff"
			state "", label: ''
		}

		main("temp2")
        
		details( ["temperature", "thermostatMode", 
				  "coolBtn", "heatBtn", "autoBtn", "offBtn",
				  "heatingSetpointDown", "heatingSetpoint", "heatingSetpointUp",
				  //"heatSliderControl", "refresh",
				  "coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp",
				  //"coolSliderControl"
                ] )
	}
}

def thermostatModes() { 
	['cool', 'heat', 'auto', 'off']
}

def shouldReportInCentigrade() {
	try {
    	def ts = getTemperatureScale();
    	return ts == "C"
    } catch (e) {
    	log.error e
    }
    return true;
}

def installed() {
    log.trace "Executing 'installed'"
    initialize()
}

def configure() {
    log.trace "Executing 'configure'"
    initialize()
}

private initialize() {
    log.trace "Executing 'initialize'"
    
    setHeatingSetpoint(defaultTemp())
	setCoolingSetpoint(defaultTemp())
    setVirtualTemperature(defaultTemp())
	setThermostatOperatingState("idle")
    setThermostatMode("off")
    sendEvent(name:"supportedThermostatModes",    value: thermostatModes(), displayed: false)
    sendEvent(name:"supportedThermostatFanModes", values: [], displayed: false)
    
	state.tempScale = "C"
}

def getTempColors() {
	def colorMap
        //getTemperatureScale() == "C"   wantMetric()
	if(shouldReportInCentigrade()) {
		colorMap = [
			// Celsius Color Range
			[value: 0, color: "#153591"],
			[value: 7, color: "#1e9cbb"],
			[value: 15, color: "#90d2a7"],
			[value: 23, color: "#44b621"],
			[value: 29, color: "#f1d801"],
			[value: 33, color: "#d04e00"],
			[value: 36, color: "#bc2323"]
			]
	} else {
		colorMap = [
			// Fahrenheit Color Range
			[value: 40, color: "#153591"],
			[value: 44, color: "#1e9cbb"],
			[value: 59, color: "#90d2a7"],
			[value: 74, color: "#44b621"],
			[value: 84, color: "#f1d801"],
			[value: 92, color: "#d04e00"],
			[value: 96, color: "#bc2323"]
		]
	}
}

def unitString() {  return shouldReportInCentigrade() ? "C": "F" }
def defaultTemp() { return shouldReportInCentigrade() ? 20 : 70 }
def lowRange() { return shouldReportInCentigrade() ? 9 : 45 }
def highRange() { return shouldReportInCentigrade() ? 45 : 113 }
def getRange() { return "${lowRange()}..${highRange()}" }

def getTemperature() {
	return device.currentValue("temperature")
}

def setHeatingSetpoint(temp) {
	def ctsp = device.currentValue("thermostatSetpoint");
    def chsp = device.currentValue("heatingSetpoint");

    if(ctsp != temp || chsp != temp) {
        sendEvent(name:"thermostatSetpoint", value: temp, unit: unitString(), displayed: false)
        sendEvent(name:"heatingSetpoint", value: temp, unit: unitString())
        sendEvent(name:"coolingSetpoint", value: temp, unit: unitString())
    }
}

def heatingSetpointUp() {
	def hsp = device.currentValue("thermostatSetpoint")
	if(hsp + 1.0 > highRange()) return;
	setHeatingSetpoint(hsp + 1.0)
}

def heatingSetpointDown() {
	def hsp = device.currentValue("thermostatSetpoint")
	if(hsp - 1.0 < lowRange()) return;
	setHeatingSetpoint(hsp - 1.0)
}

def setCoolingSetpoint(temp) {
	def ctsp = device.currentValue("thermostatSetpoint");
	def ccsp = device.currentValue("coolingSetpoint");

	if(ctsp != temp || ccsp != temp) {
		sendEvent(name:"thermostatSetpoint", value: temp, unit: unitString(), displayed: false)
		sendEvent(name:"coolingSetpoint", value: temp, unit: unitString())
		sendEvent(name:"heatingSetpoint", value: temp, unit: unitString())
	}
}

def coolingSetpointUp() {
	def csp = device.currentValue("thermostatSetpoint")
	if(csp + 1.0 > highRange()) return;
	setCoolingSetpoint(csp + 1.0)
}

def coolingSetpointDown() {
	def csp = device.currentValue("thermostatSetpoint")
	if(csp - 1.0 < lowRange()) return;
	setCoolingSetpoint(csp - 1.0)
}

def levelUp() {
	def hsp = device.currentValue("thermostatSetpoint")
	if(hsp + 1.0 > highRange()) return;
    setHeatingSetpoint(hsp + 1.0)
}

def levelDown() {
    def hsp = device.currentValue("thermostatSetpoint")
	if(hsp - 1.0 < lowRange()) return;
    setHeatingSetpoint(hsp - 1.0)
}

def parse(data) {
    log.debug "parse data: $data"
}

def refresh() {
    log.trace "Executing refresh"
    sendEvent(name: "supportedThermostatModes",    value: thermostatModes(), displayed: false)
    sendEvent(name: "supportedThermostatFanModes", values: [], displayed: false)
}

def getThermostatMode() {
	return device.currentValue("thermostatMode")
}

def getOperatingState() {
	return device.currentValue("thermostatOperatingState")
}

def getThermostatSetpoint() {
	return device.currentValue("thermostatSetpoint")
}

def getHeatingSetpoint() {
	return device.currentValue("heatingSetpoint")
}

def getCoolingSetpoint() {
	return device.currentValue("coolingSetpoint")
}

def poll() {
}

def offbtn() {
	setThermostatMode("off")
}

def coolbtn() {
	setThermostatMode("cool")
}

def heatbtn() {
	setThermostatMode("heat")
}

def autobtn() {
	setThermostatMode("auto")
}

def setThermostatMode(mode) {
	log.trace "setting thermostat mode $mode"
	if(device.currentValue("thermostatMode") != mode) {
    	sendEvent(name: "thermostatMode", value: mode)
    }
}

def levelUpDown() {
}

def changeMode() {
    def modes = thermostatModes()
	def newModeIdx = (modes.indexOf(device.currentValue("thermostatMode")) + 1) % modes.size()
	def newMode = modes[newModeIdx]
	setThermostatMode(newMode)
    return newMode
}

def setVirtualTemperature(temp) {
	sendEvent(name:"temperature", value: temp, unit: unitString(), displayed: true)
}

def setThermostatOperatingState(string) {
	if(device.currentValue("thermostatOperatingState") != string) {
		sendEvent(name:"thermostatOperatingState", value: string)
    }
}
