package luna.common.context;

import luna.util.StringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class ElasticContext {
    private boolean sniff;
    private boolean compress;
    private String clusterName;
    private List<String> hosts;

    public boolean isSniff() {
        return sniff;
    }

    public void setSniff(boolean sniff) {
        this.sniff = sniff;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, StringStyle.DEFAULT_STYLE);
    }
}
