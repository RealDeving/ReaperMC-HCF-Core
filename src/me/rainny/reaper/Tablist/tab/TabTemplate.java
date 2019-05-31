package me.rainny.reaper.Tablist.tab;

import java.util.ArrayList;
import java.util.List;

public class TabTemplate
{
  private final List<String> left;
  private final List<String> middle;
  private final List<String> right;
  private final List<String> farRight;
  
  public List<String> getLeft()
  {
    return this.left;
  }
  
  public List<String> getMiddle()
  {
    return this.middle;
  }
  
  public List<String> getRight()
  {
    return this.right;
  }
  
  public List<String> getFarRight()
  {
    return this.farRight;
  }
  
  public TabTemplate()
  {
    this.left = new ArrayList<String>();
    this.middle = new ArrayList<String>();
    this.right = new ArrayList<String>();
    this.farRight = new ArrayList<String>();
  }
  
  public TabTemplate farRight(String string)
  {
    return farRight(this.farRight.size(), string);
  }
  
  public TabTemplate farRight(int index, String string)
  {
    if (index > this.farRight.size()) {
      for (int i = this.farRight.size(); i < index; i++) {
        this.farRight.add("");
      }
    }
    this.farRight.add(index, string);
    return this;
  }
  
  public TabTemplate left(String string)
  {
    return left(this.left.size(), string);
  }
  
  public TabTemplate middle(String string)
  {
    return middle(this.middle.size(), string);
  }
  
  public TabTemplate right(String string)
  {
    return right(this.right.size(), string);
  }
  
  public TabTemplate left(int index, String string)
  {
    if (index > this.left.size()) {
      for (int i = this.left.size(); i < index; i++) {
        this.left.add("");
      }
    }
    this.left.add(index, string);
    return this;
  }
  
  public TabTemplate middle(int index, String string)
  {
    if (index > this.middle.size()) {
      for (int i = this.middle.size(); i < index; i++) {
        this.middle.add("");
      }
    }
    this.middle.add(index, string);
    return this;
  }
  
  public TabTemplate right(int index, String string)
  {
    if (index > this.right.size()) {
      for (int i = this.right.size(); i < index; i++) {
        this.right.add("");
      }
    }
    this.right.add(index, string);
    return this;
  }
}
