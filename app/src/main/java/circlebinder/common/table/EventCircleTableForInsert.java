package circlebinder.common.table;

import android.text.TextUtils;

import circlebinder.common.event.Circle;

public final class EventCircleTableForInsert {

    public static class Builder {
        private long blockId;
        private int spaceNo;
        private int spaceNoSub;
        private String circleName;
        private String penName;
        private String homepage;
        private int checklistId;

        public Builder() {}

        public Builder(Circle circle) {
            this.blockId = circle.getSpace().getBlockId();
            this.spaceNo = circle.getSpace().getNo();
            this.spaceNoSub = TextUtils.equals(circle.getSpace().getNoSub(), "a") ? 0 : 1;
            this.circleName = circle.getName();
            this.penName = circle.getPenName();
            this.homepage = TextUtils.join("\t", circle.getLinks().toList());
        }

        public EventCircleTableForInsert build() {
            return new EventCircleTableForInsert(this);
        }

        public Builder setSpaceNo(int spaceNo) {
            this.spaceNo = spaceNo;
            return this;
        }

        public Builder setBlockId(long blockId) {
            this.blockId = blockId;
            return this;
        }

        public Builder setSpaceNoSub(int spaceNoSub) {
            this.spaceNoSub = spaceNoSub;
            return this;
        }

        public Builder setCircleName(String circleName) {
            this.circleName = circleName;
            return this;
        }

        public Builder setPenName(String penName) {
            this.penName = penName;
            return this;
        }

        public Builder setHomepage(String homepage) {
            this.homepage = homepage;
            return this;
        }

        public Builder setChecklistId(int checklistId) {
            this.checklistId = checklistId;
            return this;
        }

    }

    private final long blockId;
    private final int spaceNo;
    private final int spaceNoSub;
    private final String circleName;
    private final String penName;
    private final String homepage;
    private final int checklistId;

    private EventCircleTableForInsert(Builder builder) {
        this.blockId = builder.blockId;
        this.spaceNo = builder.spaceNo;
        this.spaceNoSub = builder.spaceNoSub;
        this.circleName = builder.circleName;
        this.penName = builder.penName;
        this.homepage = builder.homepage;
        this.checklistId = builder.checklistId;
    }

    public long getBlockId() {
        return blockId;
    }

    public int getSpaceNo() {
        return spaceNo;
    }

    public int getSpaceNoSub() {
        return spaceNoSub;
    }

    public String getCircleName() {
        return circleName;
    }

    public String getPenName() {
        return penName;
    }

    public String getHomepage() {
        return homepage;
    }

    public int getChecklistId() {
        return checklistId;
    }
    
    @Override
    public boolean equals(Object object) {
        return object != null
                && object instanceof EventCircleTableForInsert
                && TextUtils.equals(penName, ((EventCircleTableForInsert)object).getPenName())
                && TextUtils.equals(circleName, ((EventCircleTableForInsert)object).getCircleName())
                && blockId == ((EventCircleTableForInsert)object).getBlockId()
                && spaceNo == ((EventCircleTableForInsert)object).spaceNo
                && spaceNoSub == ((EventCircleTableForInsert)object).spaceNoSub;
    }
    
}
