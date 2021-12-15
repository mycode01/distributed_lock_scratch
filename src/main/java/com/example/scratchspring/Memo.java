package com.example.scratchspring;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Memo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String author;
  private String content;

  public Memo(String author, String content) {
    this.author = author;
    this.content = content;
  }

  protected Memo(){}

  public Long getId() {
    return id;
  }

  public String getAuthor() {
    return author;
  }

  public String getContent() {
    return content;
  }

  public void addContent(String additionalContent){
    content = content + additionalContent;
  }
}
