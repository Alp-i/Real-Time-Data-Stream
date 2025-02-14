package com.project.homework2.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "emp")
public class Emp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    public Integer empno;
    @Column
    public String ename;
    @Column
    public String job;
    @Column
    public Integer mgr;
    @Column
    public String hiredate;
    @Column
    public Integer sal;
    @Column
    public Integer comm;
    @Column
    public Integer deptno;
    @Column
    public String img;
}
