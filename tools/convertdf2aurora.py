import sys
import os
import xml.etree.ElementTree as ET

imageFileName = ''

sourceFile = sys.argv[1]
print('Converting ' + sourceFile + '...')
tree = ET.parse(sourceFile)
root = tree.getroot()

if root.tag == 'img':
	imageFileName = root.attrib['name']

destFile = os.path.splitext(sourceFile)[0] + '.sprite'	
with open(destFile, 'w') as file:
	file.write('{')
	file.write('VERSION 0001\n')
	file.write("IMAGE 0x0000 \".\\" + imageFileName + "\" TRANSP 0x00FF00FF\n")	
	
	file.write('MODULES\n')
	file.write('{\n')	
	sprIndex = 0x1000	
	definitions = root.find('definitions')
	for spr in definitions.find('dir'):		
		file.write('MD\t' + hex(sprIndex).upper() + '\tMD_IMAGE\t0\t' + spr.attrib['x'] + '\t' + spr.attrib['y'] + '\t' + spr.attrib['w'] + '\t' + spr.attrib['h'] + '\n')
		sprIndex += 1
	file.write('}\n')
	
	sprIndex = 0x1000	
	frmIndex = 0x2000
	
	for spr in definitions.find('dir'):
		file.write("FRAME \"\"\n")
		file.write('{\n')
		file.write(hex(frmIndex).upper() + '\n')
		file.write('FM\t' + hex(sprIndex).upper() + '\t' + '-' + str(int(float(spr.attrib['w']) / 2)) + '\t' + '-' + spr.attrib['h'] + '\n')
		file.write('}\n')
		sprIndex += 1
		frmIndex += 1
		
	file.write('SPRITE_END\n')	
	file.write('}')
