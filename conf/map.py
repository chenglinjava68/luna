import MySQLdb
import ConfigParser
import time

cp = ConfigParser.SafeConfigParser()
cp.read("mysql.conf")
db_host='127.0.0.1'
db_user='root'
db_port=3306
db_password=''
db_db='all'
db_table='all'

if cp.has_option('db','host'):
    db_host = cp.get('db','host')
if cp.has_option('db','port'):
    db_port = cp.getint('db','port')
if cp.has_option('db','user'):
    db_user = cp.get('db','user')
if cp.has_option('db','password'):
    db_password = cp.get('db','password')
if cp.has_option('db','db'):
    db_db = cp.get('db','db')
if cp.has_option('db','table'):
    db_table = cp.get('db','table')
if db_db == 'all':
    db = MySQLdb.connect(host=db_host,user=db_user,passwd=db_password,port=db_port)
    cursor = db.cursor()
    try:
        sql = "show databases;"
        cursor.execute(sql)
        databases = cursor.fetchall()
        for database in databases:
            sql = "use"+database+ ";show tables;"
            cursor.execute(sql)
            tables = cursor.fetchall()
            with open("mapping.yml","ab") as f:
                f.write("\nmapping:\n")
                for table in tables:
                    f.write("  - "+table[0]+":\n      "+database+":\n")
                    f.write("        dynamic: strict\n")
                    f.write("        properties:\n")
                    cursor.execute("DESC "+table[0]+";")
                    results = cursor.fetchall()
                    for row in results:
                        name = row[0]
                        type = row[1]
                        default = row[4]
                        type = type.split('(')[0]
                        f.write("          "+row[0]+":\n")
                        if type=="int" or type=="tinyint" or type=="smallint" or type=="mediumint" :
                            f.write("            type: integer\n")
                        elif type=="float" or type=="double" :
                            f.write("            type: double\n")
                        else:
                            f.write("            type: keyword\n")
                            if default is None or default is "" or default == "0000-00-00 00:00:00" or default == "0000-00-00":
                                f.write('            null_value: "'+'"\n')
                            else:
                                f.write("            null_value: "+default+"\n")
                            f.write("            ignore_above: 256\n")
                            f.write("            store: true\n")
            f.close()
    except BaseException,e:
            print e.args
    db.close()
else:
    db = MySQLdb.connect(host=db_host,user=db_user,passwd=db_password,port=db_port,db=db_db)
    if db_table == 'all':
        cursor = db.cursor()
        sql = "show tables;"
        try:
            cursor.execute(sql)
            tables = cursor.fetchall()
            with open("mapping.yml","ab") as f:
                f.write("\nmapping:\n")
                for table in tables:
                    f.write("  - "+table[0]+":\n      "+db_db+":\n")
                    f.write("        dynamic: strict\n")
                    f.write("        properties:\n")
                    cursor.execute("DESC "+table[0]+";")
                    results = cursor.fetchall()
                    for row in results:
                        name = row[0]
                        type = row[1]
                        default = row[4]
                        type = type.split('(')[0]
                        f.write("          "+row[0]+":\n")
                        if type=="int" or type=="tinyint" or type=="smallint" or type=="mediumint" :
                            f.write("            type: integer\n")
                        elif type=="float" or type=="double" :
                            f.write("            type: double\n")
                        else:
                            f.write("            type: keyword\n")
                            if default is None or default is "" or default == "0000-00-00 00:00:00" or default == "0000-00-00":
                                f.write('            null_value: "'+'"\n')
                            else:
                                f.write("            null_value: "+default+"\n")
                            f.write("            ignore_above: 256\n")
                            f.write("            store: true\n")
                f.close()
        except BaseException,e:
            print e.args
    else:
        tables = db_table.split(',')
        cursor = db.cursor()
        with open("mapping.yml","ab") as f:
            f.write("\nmapping:\n")
            for table in tables:
               f.write("  - "+table+":\n      "+db_db+":\n")
               f.write("        dynamic: strict\n")
               f.write("        properties:\n")
               cursor.execute("DESC "+table+";")
               results = cursor.fetchall()
               for row in results:
                   name = row[0]
                   type = row[1]
                   default = row[4]
                   type = type.split('(')[0]
                   f.write("          "+row[0]+":\n")
                   if type=="int" or type=="tinyint" or type=="smallint" or type=="mediumint" :
                       f.write("            type: integer\n")
                   elif type=="float" or type=="double" :
                       f.write("            type: double\n")
                   else:
                       f.write("            type: keyword\n")
                       if default is None or default is "" or default == "0000-00-00 00:00:00" or default == "0000-00-00":
                           f.write('            null_value: "'+'"\n')
                       else:
                           f.write("            null_value: "+default+"\n")
                       f.write("            ignore_above: 256\n")
                       f.write("            store: true\n")
        f.close()           
    db.close()
