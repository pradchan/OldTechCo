# variables
username = 'weblogic'
password = 'Welcome2Cloud#'
URL='t3://129.152.150.19:7001'
datasource_name='jdbc/OE'
datasource_jndi='jdbc/OE'
#datasource_url='jdbc:oracle:thin:@//oow14hol9458-db:1521/PDB1.compute-staug305744476.oraclecloud.internal'
datasource_url='jdbc:oracle:thin:@//129.152.150.34:1521/PDB1.z17gseacct2742055121.oraclecloud.internal'
datasource_username='OE'
datasource_password='oe'
datasource_target='venkyjcs_adminserver'
datasource_targetType='Server'
datasource_targetAdmin='venkyjcs_adminserver'

# Connect and Activate Edit Mode
connect(username,password,URL)
edit()
startEdit()

# Create DataSource jdbc/OE
cd('/')
cmo.createJDBCSystemResource('OE')

cd('/JDBCSystemResources/OE/JDBCResource/OE')
cmo.setName(datasource_name)

cd('/JDBCSystemResources/OE/JDBCResource/OE/JDBCDataSourceParams/OE')
set('JNDINames',jarray.array([String(datasource_jndi)], String))

cd('/JDBCSystemResources/OE/JDBCResource/OE/JDBCDriverParams/OE')
cmo.setUrl(datasource_url)
cmo.setDriverName('oracle.jdbc.xa.client.OracleXADataSource')
cmo.setPassword('Welcome1')

cd('/JDBCSystemResources/OE/JDBCResource/OE/JDBCConnectionPoolParams/OE')
cmo.setTestTableName('SQL ISVALID\r\n\r\n')

cd('/JDBCSystemResources/OE/JDBCResource/OE/JDBCDriverParams/OE/Properties/OE')
cmo.createProperty('user')
cd('/JDBCSystemResources/OE/JDBCResource/OE/JDBCDriverParams/OE/Properties/OE/Properties/user')
cmo.setValue('DEV_MDS')

cd('/JDBCSystemResources/OE/JDBCResource/OE/JDBCDataSourceParams/OE')
cmo.setGlobalTransactionsProtocol('TwoPhaseCommit')

cd('/JDBCSystemResources/OE')
set('Targets',jarray.array([ObjectName('com.bea:Name='+datasource_targetAdmin+',Type=Server'),ObjectName('com.bea:Name='+datasource_target+',Type='+datasource_targetType)], ObjectName))

activate()

