package io.nexusbot.componentsData;

public class ChannelOverrides {
    private String id;
    private String type;
    private long allow;
    private long deny;

    public ChannelOverrides(String id, String type, long allow, long deny) {
        this.id = id;
        this.type = type;
        this.allow = allow;
        this.deny = deny;
    }

    public ChannelOverrides() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAllow() {
        return allow;
    }

    public void setAllow(long allow) {
        this.allow = allow;
    }

    public long getDeny() {
        return deny;
    }

    public void setDeny(long deny) {
        this.deny = deny;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + (int) (allow ^ (allow >>> 32));
        result = prime * result + (int) (deny ^ (deny >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChannelOverrides other = (ChannelOverrides) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (allow != other.allow) {
            return false;
        }
        if (deny != other.deny) {
            return false;
        }
        return true;
    }
}
