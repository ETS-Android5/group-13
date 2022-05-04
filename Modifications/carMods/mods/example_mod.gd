extends Reference

var mod_name: String = "Spatial"

func init(global) -> void:
	global.register_environment("example/Spatial", load("res://src/environments/example/Spatial.tscn"))
	global.register_vehicle("CoolSensorCar", load("res://src/vehicles/stupid_car/CoolSensorCar.tscn"))
