#!/usr/bin/env python2

# python2 version
import sys
import datetime
import time
# python2
import xmlrpclib
from SimpleXMLRPCServer import SimpleXMLRPCServer
import modbus_tk.defines as cst
from modbus_tk import modbus_tcp
import traceback

slave = 1
master = modbus_tcp.TcpMaster("127.0.0.1", 502, 5)
master.set_timeout(5)


def connect(ip):
    try:
        global master
        disconnect()
        master = modbus_tcp.TcpMaster(ip, 502, 5)
        master.set_timeout(5)
        # return 0
        return get_running_status()
    except Exception as e:
        print("connect: " + str(e))
        return -1


def disconnect():
    try:
        global master
        master.close()
        return 0
    except:
        return -1


def switch_mode(value):
    try:
        global master
        print(value)
        if value == 0:
            # master.execute(slave, cst.WRITE_SINGLE_COIL, 160, output_value=10)
            master.execute(slave, cst.WRITE_SINGLE_REGISTER, 160, output_value=10)
        else:
            # master.execute(slave, cst.WRITE_SINGLE_COIL, 160, output_value=20)
            master.execute(slave, cst.WRITE_SINGLE_REGISTER, 160, output_value=20)
        return 0
        return get_mode()
    except Exception as e:
        # traceback.print_exc()
        print("switch_mode: " + str(e))
        return -1


def get_mode():
    try:
        global master
        # ret = master.execute(slave, cst.READ_DISCRETE_INPUTS, 160, 1)
        ret = master.execute(slave, cst.READ_HOLDING_REGISTERS, 160, 1)
        return ret[0]
    except Exception as e:
        print("get_mode:" + str(e))
        return -1


def lift_up(value):
    try:
        global master
        master.execute(slave, cst.WRITE_SINGLE_COIL, 0, output_value=int(value))
        if value:
            master.execute(slave, cst.WRITE_SINGLE_COIL, 1, output_value=int(not value))
        return 0
    except Exception as e:
        print("lift_up: " + str(e))
        return -1


def lift_down(value):
    try:
        global master
        master.execute(slave, cst.WRITE_SINGLE_COIL, 1, output_value=int(value))
        if value:
            master.execute(slave, cst.WRITE_SINGLE_COIL, 0, output_value=int(not value))
        return 0
    except Exception as e:
        print("lift_down: " + str(e))
        return -1


def set_target_pos(value):
    try:
        global master
        master.execute(slave, cst.WRITE_SINGLE_REGISTER, 100, output_value=value)
        return 0
    except:
        return -1


def calibrate():
    try:
        lift_down(True)
        while 1:
            old = get_current_pos()
            datetime.time.sleep(1)
            now = get_current_pos()
            if old == now:
                global master
                master.execute(slave, cst.WRITE_SINGLE_COIL, 2049, output_value=1)
                datetime.time.sleep(1)
                master.execute(slave, cst.WRITE_SINGLE_COIL, 2049, output_value=0)
                ret = get_calibration_status()
                if ret[0] == 0:
                    return 0
                else:
                    return -1
            time.sleep(1)
    except:
        return -1


def get_target_pos():
    try:
        global master
        ret = master.execute(slave, cst.READ_HOLDING_REGISTERS, 130, 1)
        return ret[0]
    except:
        return -1


def get_current_pos():
    try:
        global master
        ret = master.execute(slave, cst.READ_HOLDING_REGISTERS, 110, 1)
        return ret[0]
    except:
        return -1


def get_running_status():
    try:
        global master
        ret = master.execute(slave, cst.READ_DISCRETE_INPUTS, 2048, 1)
        return ret[0]
    except Exception as e:
        print("get_running_status: " + str(e))
        return -1


def get_calibration_status():
    try:
        global master
        ret = master.execute(slave, cst.READ_HOLDING_REGISTERS, 2049, 1)
        return ret[0]
    except:
        return -1


def stop():
    print("stop lift")
    try:
        global master
        ret = master.execute(slave, cst.WRITE_SINGLE_COIL, 2, 0)
        return ret[0]
    except:
        return -1


def cancel_stop():
    print("stop lift")
    try:
        global master
        ret = master.execute(slave, cst.WRITE_SINGLE_COIL, 2, 1)
        return ret[0]
    except:
        return -1


port = 9999
server = SimpleXMLRPCServer(("127.0.0.1", 9999), allow_none=True)

server.register_function(stop, "stop")
server.register_function(cancel_stop, "cancel_stop")
server.register_function(connect, "connect")
server.register_function(disconnect, "disconnect")
server.register_function(switch_mode, "switch_mode")
server.register_function(lift_up, "lift_up")
server.register_function(lift_down, "lift_down")
server.register_function(set_target_pos, "set_target_pos")
server.register_function(calibrate, "calibrate")
server.register_function(get_mode, "get_mode")
server.register_function(get_target_pos, "get_target_pos")
server.register_function(get_current_pos, "get_current_pos")
server.register_function(get_running_status, "get_running_status")
server.register_function(get_calibration_status, "get_calibration_status")

print("Listening on port {}...".format(port))
server.serve_forever()
