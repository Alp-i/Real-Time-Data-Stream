package com.project.homework2.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpDTO {
    public Long id;
    public int empno;
    public String ename;
    public String mname;
    public int sal;
    public Integer comm;
    public String dname;
    public String img;
    public String job;
    public Integer mgr;
    public String hiredate;
    public Integer deptno;
    public Double totalPayment;

    public EmpDTO(Long id, int empno, String ename, String mname, int sal, Integer comm, String dname, String img, String job, Integer mgr, String hiredate, Integer deptno) {
        this.id = id;
        this.empno = empno;
        this.ename = ename;
        this.mname = mname;
        this.sal = sal;
        this.comm = comm;
        this.dname = dname;
        this.img = img;
        this.job = job;
        this.mgr = mgr;
        this.hiredate = hiredate;
        this.deptno = deptno;
    }
}
