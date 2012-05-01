import yaml;

def streamInYAML(stream):
        y = ''
        cont = 1
        while cont:
                l = stream.readline()
                if len(l) == 0:
                        cont = 0
                else:
                        if l.startswith('---'):
                                print yaml.load(y)
                                print '\n'
                                y = ''
                        else:
		                 y = y + l
        print yaml.load(y)
        print '\n'

f=open('test.yaml')
streamInYAML(f)

