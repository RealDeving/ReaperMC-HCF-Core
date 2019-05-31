package me.rainny.reaper.armors.archer;

import javax.annotation.Nonnull;
import org.bukkit.scheduler.BukkitTask;

public class ArcherMark
        implements Comparable<ArcherMark>
{
    public BukkitTask decrementTask;
    public int currentLevel;

    public void reset()
    {
        if (this.decrementTask != null)
        {
            this.decrementTask.cancel();
            this.decrementTask = null;
        }
        this.currentLevel = 0;
    }

    public int incrementMark()
    {
        return ++this.currentLevel;
    }

    public int decrementMark()
    {
        return --this.currentLevel;
    }

    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArcherMark)) {
            return false;
        }
        ArcherMark that = (ArcherMark)o;
        if (this.currentLevel != that.currentLevel) {
            return false;
        }
        return this.decrementTask != null ? this.decrementTask.equals(that.decrementTask) : that.decrementTask == null;
    }

    public int hashCode()
    {
        int result = this.decrementTask != null ? this.decrementTask.hashCode() : 0;
        result = 31 * result + this.currentLevel;
        return result;
    }

    public int compareTo(@Nonnull ArcherMark archerMark)
    {
        return Integer.compare(this.currentLevel, archerMark.currentLevel);
    }
}
