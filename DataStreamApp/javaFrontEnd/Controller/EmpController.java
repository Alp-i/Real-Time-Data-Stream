package com.project.homework2.Controller;

import com.project.homework2.Model.Emp;
import com.project.homework2.Model.EmpDTO;
import com.project.homework2.Repository.EmployeeRepository;
import com.project.homework2.ExpenseService;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.hadoop.conf.Configuration;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class EmpController {
    private final EmployeeRepository employeeRepository;
    private final ExpenseService expenseService;

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        if (employeeRepository.existsById(id)){
            Emp emp = employeeRepository.findById(id).orElse(null);
            if (emp != null) {
                String fileName = emp.getImg();
                employeeRepository.deleteById(id);
                deleteImageFromHdfs(fileName);
            }
        } else{
            System.out.println("Employee with ID " + id + " does not exist.");
        }
        return "redirect:/";
    }

    @PostMapping("/add")
    public String addEmployee(@ModelAttribute Emp emp, @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            uploadImage(file, emp);
        }
        employeeRepository.save(emp);
        return "redirect:/";
    }

    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable() Long id, @ModelAttribute Emp emp, @RequestParam("file") MultipartFile file) {
        Optional<Emp> existingEmployeeOpt = employeeRepository.findById(id);
        if (existingEmployeeOpt.isPresent()) {
            Emp existingEmployee = existingEmployeeOpt.get();

            if (emp.getEname() != null && !emp.getEname().isEmpty()) {
                existingEmployee.setEname(emp.getEname());
            }
            if (emp.getJob() != null && !emp.getJob().isEmpty()) {
                existingEmployee.setJob(emp.getJob());
            }
            if (emp.getMgr() != null) {
                existingEmployee.setMgr(emp.getMgr());
            }
            if (emp.getHiredate() != null && !emp.getHiredate().isEmpty()) {
                existingEmployee.setHiredate(emp.getHiredate());
            }
            if (emp.getSal() != null) {
                existingEmployee.setSal(emp.getSal());
            }
            if (emp.getComm() != null) {
                existingEmployee.setComm(emp.getComm());
            }
            if (emp.getDeptno() != null) {
                existingEmployee.setDeptno(emp.getDeptno());
            }
            if (emp.getEmpno() != null) {
                existingEmployee.setEmpno(emp.getEmpno());
            }

            if (!file.isEmpty()) {
                deleteImageFromHdfs(existingEmployee.getImg());
                uploadImage(file, existingEmployee);
            }

            employeeRepository.save(existingEmployee);
        }
        return "redirect:/";
    }

    @GetMapping("/")
    public String getEmps(Model model){
        Dataset<Row> totalPayments = expenseService.getTotalPaymentByEmpno();

        Map<Integer, Double> paymentMap = totalPayments.collectAsList().stream()
                .collect(Collectors.toMap(
                        row -> row.getAs("userid"),
                        row -> {
                            Double payment = row.getAs("total_payment");
                            return Math.round(payment * 100.0) / 100.0;
                        }
                ));

        List<EmpDTO> empDTOList = employeeRepository.findAllEmployeesWithDeptAndMgr().stream()
                .peek(emp -> emp.setTotalPayment(paymentMap.getOrDefault(emp.getEmpno(), 0.0)))
                .collect(Collectors.toList());

        model.addAttribute("empList", empDTOList);
        return "index";
    }

    private void deleteImageFromHdfs(String fileName) {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://127.0.0.1:9000");

        try (FileSystem fileSystem = FileSystem.get(conf)) {
            Path hdfsFilePath = new Path("/" + fileName);
            if (fileSystem.exists(hdfsFilePath)) {
                fileSystem.delete(hdfsFilePath, false);
                System.out.println("Deleted image from HDFS: " + fileName);
            } else {
                System.out.println("Image does not exist in HDFS: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Error while deleting file from HDFS: " + e.getMessage());
        }
    }

    private void uploadImage(MultipartFile file, Emp existingEmp) {
        String newFileName = file.getOriginalFilename();
        existingEmp.setImg(newFileName);

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://127.0.0.1:9000");

        try (FileSystem fileSystem = FileSystem.get(conf);
             FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path("/" + newFileName), true);
             InputStream inputStream = file.getInputStream()) {

            IOUtils.copy(inputStream, fsDataOutputStream);
            System.out.println("Uploaded: " + newFileName);

        } catch (IOException e) {
            System.out.println("File save error! " + e.getMessage());
        }
    }
}
