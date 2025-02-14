package com.project.homework2.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dept")
public class Dept {
    @Id
    public int deptno;

    @Column
    public String dname;
    @Column
    public String loc;
}
