package luna.common.model;

import java.util.Map;

public class Record {
    private String index;
    private String type;
    private String id;
    private Map<String,Object> data;
    private OperateType operateType;

    public Record(String index,String type,String id,Map<String,Object> data,OperateType operateType){
        this.index=index;
        this.type=type;
        this.id=id;
        this.data=data;
        this.operateType=operateType;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String,?> getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }
}
