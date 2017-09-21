try:
    with open("time.log") as f:
        sum=0
        num=0
        max=0
        line = f.readline()
        while line :
            if max < int(line.split(" ")[4]):
                max = int(line.split(" ")[4])
            sum += int(line.split(" ")[4])
            num += 1
            line = f.readline()
        print(sum/num)
        print(num)
        print(max)
    f.close()
except BaseException,e:
    print e.args
        
