package io.nexusbot.componentsData;

import java.util.EnumSet;

import net.dv8tion.jda.api.Permission;

public class ChannelOverrides {
    private String type;
    private EnumSet<Permission> allow;
    private EnumSet<Permission> deny;

    public ChannelOverrides(String type, EnumSet<Permission> allow, EnumSet<Permission> deny) {
        this.type = type;
        this.allow = allow;
        this.deny = deny;
    }

    public ChannelOverrides() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EnumSet<Permission> getAllow() {
        return allow;
    }

    public void setAllow(EnumSet<Permission> allow) {
        this.allow = allow;
    }

    public EnumSet<Permission> getDeny() {
        return deny;
    }

    public void setDeny(EnumSet<Permission> deny) {
        this.deny = deny;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((allow == null) ? 0 : allow.hashCode());
        result = prime * result + ((deny == null) ? 0 : deny.hashCode());
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
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (allow == null) {
            if (other.allow != null) {
                return false;
            }
        } else if (!allow.equals(other.allow)) {
            return false;
        }
        if (deny == null) {
            if (other.deny != null) {
                return false;
            }
        } else if (!deny.equals(other.deny)) {
            return false;
        }
        return true;
    }
}
