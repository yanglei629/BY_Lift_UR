#!/usr/bin/env python2
import sys

print(sys.path)
# python2 version
import modbus_tk.defines as cst
import socket
import threading
import time
# python2
from SimpleXMLRPCServer import SimpleXMLRPCServer
from modbus_tk import modbus_tcp
import ctypes

is_connected = False
slave = 1
# master = modbus_tcp.TcpMaster("127.0.0.1", 502, 5)
master = None
# master = modbus_tcp.TcpMaster("192.168.1.5", 502, 5)
# master.set_timeout(5)

dash = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# PLAYING PAUSED STOPPED
pre_program_state = ''
current_program_state = ''


def get_program_state():
    try:
        global dash
        # outdata = 'programState\n'
        outdata = 'running\n'
        dash.send(outdata.encode())

        indata = dash.recv(1024)
        print('recv: ' + indata.decode())
        # return indata.decode().split(' ')[0]
        return indata.decode().split(':')[1].strip()
    except Exception as e:
        print(str(e))
        # return get_program_state()
        return -1


def connect(ip):
    try:
        global master
        global is_connected
        disconnect()
        master = modbus_tcp.TcpMaster(ip, 502, 5)
        master.set_timeout(5)

        ret = get_running_status()
        if ret != -1:
            is_connected = True
            threading.Thread(target=monitor_program_state, args=()).start()

        # return 0
        return ret
    except Exception as e:
        print("connect: " + str(e))
        return -1


def disconnect():
    try:
        global master
        global is_connected
        master.close()

        # master = modbus_tcp.TcpMaster("127.0.0.1", 502, 5)
        master = None
        is_connected = False
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
    cancel_stop()
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
    cancel_stop()
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
    print("set_target_pos:" + str(value))
    cancel_stop()
    try:
        global master
        master.execute(slave, cst.WRITE_SINGLE_REGISTER, 100, output_value=value)
        return 0
    except:
        return -1


def calibrate():
    print("[WARNING] execute calibrate")
    cancel_stop()
    try:
        global master
        master.execute(slave, cst.WRITE_SINGLE_COIL, 2049, output_value=1)
        time.sleep(1)
        master.execute(slave, cst.WRITE_SINGLE_COIL, 2049, output_value=0)
        ret = get_calibration_status()
        if ret[0] == 0:
            return 0
        else:
            return -1
    except:
        return -1


def get_target_pos():
    try:
        global master
        # ret = master.execute(slave, cst.READ_HOLDING_REGISTERS, 130, 1)
        ret = master.execute(slave, cst.READ_HOLDING_REGISTERS, 100, 1)
        return ret[0]
    except:
        return -1


def get_current_pos():
    try:
        global master
        # ret = master.execute(slave, cst.READ_HOLDING_REGISTERS, 110, 1)
        ret = master.execute(slave, cst.READ_HOLDING_REGISTERS, 130, 1)
        # return ret[0]
        # unsigned to signed
        return ctypes.c_int16(ret[0]).value
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
        # ret = master.execute(slave, cst.WRITE_SINGLE_COIL, 2, 0)
        ret = master.execute(slave, cst.WRITE_SINGLE_COIL, 2, output_value=0)
        return ret[0]
    except:
        return -1


def cancel_stop():
    print("cancel stop lift")
    try:
        global master
        # ret = master.execute(slave, cst.WRITE_SINGLE_COIL, 2, 1)
        ret = master.execute(slave, cst.WRITE_SINGLE_COIL, 2, output_value=1)
        return ret[0]
        # return 0
    except:
        return -1


def monitor_program_state():
    global current_program_state
    global pre_program_state
    global is_connected
    HOST = '127.0.0.1'
    PORT = 29999

    try:
        dash.connect((HOST, PORT))
        recv = dash.recv(1024)
        print('recv: ' + recv.decode())
    except:
        print("[WARNING] build socket with robot failed.....")

    print("[WARING] Start Monitor Program State...........")
    pre_program_state = get_program_state()
    while is_connected:
        res = get_program_state()
        if res == -1:
            print("[WARING] get program state failed")
            time.sleep(1)
            continue
        current_program_state = res

        if current_program_state == 'false' and pre_program_state == 'true':
            print("[Warning] Program Stopped")
            # if is_connected:
            #     stop()
            stop()
        pre_program_state = current_program_state
        time.sleep(1)
    print("[WARNING] Monitor Thread Exit.................")


port = 9999
server = SimpleXMLRPCServer(("127.0.0.1", port), allow_none=True)

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
