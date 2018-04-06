package org.nxl.model;

import org.nxl.annotation.Column;
import org.nxl.annotation.Entity;
import org.nxl.annotation.PK;

@Entity
public class Author {
    @Column
    @PK
    private Long authorid;
    @Column
    private String name;
    @Column
    private Integer age;
    @Column
    private Long createtime;

    public Long getAuthorid() {
        return authorid;
    }

    public void setAuthorid(Long authorid) {
        this.authorid = authorid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        return "Author{" +
                "authorid=" + authorid +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", createtime=" + createtime +
                '}';
    }
}

