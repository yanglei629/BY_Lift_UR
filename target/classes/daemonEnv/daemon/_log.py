'''
Author: Elite_zhangjunjie
CreateDate: 2022-03-17 
LastEditors: Elite_zhangjunjie
LastEditTime: 2022-10-13 11:52:54
Description: 用于xmlRPCServer的日志记录
'''
import functools
import time
import inspect

# todo:函数参数和默认值已经记录


class PluginLog():

    def __init__(self, log_path: str="log.log") -> None:
        """init 日志

        Args:
            log_path (str, optional): 日志路径. Defaults to "log.log".
        """
        self._log_path = log_path
        self._log_time = 0
        self._is_logging = True


    def set_log_file(self, log_file: str):
        """修改日志的记录路径

        Args:
            log_file (str):日志路径
        """
        self._log_path = log_file
    
    
    def switch_logging(self, status: bool):
        """修改记录日志的状态

        Args:
            status (bool): True:record Flase:not record
        """
        self._is_logging = status
    
    
    def get_func_args(self, func: object, args: tuple):
        """获取函数的参数信息

        Args:
            func (object)): 函数
            args (tuple): 参数列表
        """
        # 获取函数参数返回一个有序字典
        parms = inspect.signature(func).parameters
        # 获取参数名，和参数类型
        msg_args = "Args:"
        temp = 0
        for name, parm in parms.items():
            if name == "self": 
                # print(parm)
                # print(parm,args[temp])
                temp += 1
                continue
            if temp < len(args):
                value = args[temp]
                value_type = type(value).__name__
            else:
                value = "-"
                value_type = "-"
            msg_args += str(parm).replace(" ","") +"|%s"%value_type+"|%s"%value
            temp += 1
            if temp != len(parms.items()): msg_args += "|_|"
            
        self.debug(msg_args, num_inc=False)

    
    def logit(self, func):
        """装饰器实现
        """
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            name = func.__name__
            # 获取函数参数返回一个有序字典
            self.debug("Func:%s"%name, num_inc=False)
            self.get_func_args(func, args)
            return func(*args, **kwargs)
        return wrapper


    @staticmethod
    def get_time():
        """get format data
        """
        return time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
        
            
    def debug(self, text: str, num_inc: bool=True):
        """log record

        Args:
            text (str): record txt
        """
        current_time = self.get_time()
        msg = "%s| %s |%s"%(current_time, self._log_time, text)
        print(msg)
        if self._is_logging:
            try:
                with open(self._log_path, "a+") as f:
                    if self._log_time == 0:
                        f.write("-------------------------------------------\n")
                    f.write(msg+"\n")
            except FileNotFoundError as e:
                self._log_path = "log.log"

        if num_inc: self._log_time += 1
            
            
#     def print_xmlRpcServer_info(self, host: str, port: int):
#         """打印并记录
#
#         Args:
#             host (str): rpc host
#             port (int): rpc port
#         """
#         MAX_STR_LENGTH = 30
#         MAX_PRINT_LENGTH = 40
#         t = []
#         t.append("-".center(MAX_PRINT_LENGTH, "-"))
#         t.append("XMLRPC server has been started".center(MAX_STR_LENGTH," ").center(MAX_PRINT_LENGTH, "-"))
#         t.append((f"Host: %s"%host).center(MAX_STR_LENGTH, " ").center(MAX_PRINT_LENGTH, "-"))
#         t.append((f"Port: %s"%port).center(MAX_STR_LENGTH, " ").center(MAX_PRINT_LENGTH, "-"))
#         t.append("-".center(MAX_PRINT_LENGTH, "-"))
#         [self.debug(i,num_inc=False) for i in t]


logger = PluginLog()

if __name__ == "__main__":

    @logger.logit
    def connect_gripper(a, b, c=9):
        print(a, b, c)
        print("----")
        
    def haha(a, g, e):
        pass
        
    connect_gripper(1, 4, 6)
    connect_gripper(2, 5, 5)
    connect_gripper(3, 6, 4)
    connect_gripper(4, 7, )
    
    print(connect_gripper.__name__)
    a= inspect.getfullargspec(connect_gripper)  # 获取名字
    a= inspect.signature(connect_gripper)       # 查看函数
    print(a.parameters)
    a= inspect.getfullargspec(haha)
    print(a)