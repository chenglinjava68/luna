package luna.applier;

import luna.common.model.Record;

import java.util.List;

public interface Applier {
    void applyBatch(List<Record> records);
    void applyOneByOne(Record record);
}
