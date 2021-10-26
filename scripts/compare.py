import yaml
import subprocess
import sys
import os

def read_yaml(path):
    with open(path, "r") as f:
        return yaml.safe_load(f)

def read_file(path):
    with open(path, "r") as f:
        return f.readlines()

if __name__ == '__main__':
    base_path = sys.argv[1]
    cfg_path  = sys.argv[2]

    builds_path = os.path.join(base_path, "builds")
    spec_path   = os.path.join(base_path, "spec")
    cfg = read_yaml(cfg_path)

    for name, info in cfg.items():
        for case in info:
            file_name = case + ".v"
            a_txt = os.path.join(builds_path, name, file_name)
            b_txt = os.path.join(spec_path, name, file_name)
            result = subprocess.run(
                ['diff', a_txt, b_txt], stdout=subprocess.PIPE
            ).stdout.decode('utf-8')
            if result:
                print("case %s in %s ERROR" % (case, name))
                print(result)
                sys.exit(-1)
