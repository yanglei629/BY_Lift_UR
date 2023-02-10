#! /bin/bash

#old
# BASE_DIR=$(dirname $(realpath $0))
#new
BASE_DIR=$(dirname $(readlink -f $0))
echo "${BASE_DIR}"

tar_install() {
    echo "tar_install $1 ..."
    tar -zxf $1
    cd "${1%%.tar.gz*}"
    python2 setup.py install
}

enter_venv() {
    if [[ ! -d "venv" ]]; then
#        ## install setuptools
#        echo "[INFO] install setuptools..."
#        cd "$BASE_DIR"/dependencies/
#        tar -zxf setuptools-20.7.0.tar.gz
#        cd setuptools-20.7.0/
#        python2 setup.py install
#
#        ## install pip
#        echo "[INFO] install pip2..."
#        cd "$BASE_DIR"/dependencies/
#        tar -zxf pip-20.3.4.tar.gz
#        cd pip-20.3.4/
#        python2 setup.py install
#
#        ## install virtualenv
#        echo "[INFO] install virtualenv..."
#        cd "$BASE_DIR"/dependencies/
#        pip2 install virtualenv-16.7.10-py2.py3-none-any.whl

        echo 'create venv...'
        ## python2
        cd "$BASE_DIR"
        virtualenv -p /usr/bin/python2 venv
        ###python3
        #python3 -m venv venv
        source venv/bin/activate

        ### install dependencies
        echo "install dependencies..."
        # cd "$BASE_DIR"/
        ###python2
        pip2 install "$BASE_DIR"/wheels/*.whl
        ###python3
        #pip3 install "$BASE_DIR"/wheels/*.whl
        
        for file in "$BASE_DIR"/wheels/*; do
          cd "$BASE_DIR"/wheels/
          if [ -f "$file" ]; then
            if [[ $file =~ ".tar.gz" ]]; then
              tar_install $file
            fi
          fi
      done
    else
        echo  'enter venv...'
        source venv/bin/activate
    fi
}

echo "-------------------------------------------"
echo "---------------enter venv------------------"

rm -rf "$BASE_DIR"/venv
# sleep 3s

enter_venv
# pip show pip
#pip3 list
#pip2 list


echo "-------------------------------------------"
echo "--------------start daemon-----------------"

# start python daemon program
#python3 "$BASE_DIR"/daemon/main.py
python2 -u "$BASE_DIR"/daemon/main.py

echo "-----------------done----------------------"
