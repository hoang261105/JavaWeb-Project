package com.data.repository.student;

import com.data.model.CourseStats;
import com.data.model.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public class StudentRepositoryImp implements StudentRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(Student student) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.save(student);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public boolean checkExistUserName(String userName) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(*) from Student where userName = :userName", Long.class);
            query.setParameter("userName", userName);

            Long count = query.getSingleResult();
            return count > 0;
        }finally {
            session.close();
        }
    }

    @Override
    public boolean checkExistEmail(String email) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(*) from Student where email = :email", Long.class);

            query.setParameter("email", email);

            Long count = query.getSingleResult();
            return count > 0;
        }finally {
            session.close();
        }
    }

    @Override
    public boolean checkExistPhone(String phone) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(*) from Student where phone = :phone", Long.class);

            query.setParameter("phone", phone);

            Long count = query.getSingleResult();
            return count > 0;
        }finally {
            session.close();
        }
    }

    @Override
    public Student checkLogin(String email, String password) {
        Session session = sessionFactory.openSession();

        try {
            Query<Student> query = session.createQuery("from Student where email = :email and password = :password", Student.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            List<Student> resultList = query.getResultList();
            return resultList.isEmpty() ? null : resultList.get(0);
        }finally {
            session.close();
        }
    }

    @Override
    public List<Student> findAll(int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            Query<Student> query = session.createQuery("from Student where role = false", Student.class);

            if (page < 1) page = 1;

            query.setFirstResult((page - 1) *  size);
            query.setMaxResults(size);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public long count() {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(*) from Student where role = false", Long.class);

            return query.getSingleResult();
        }finally {
            session.close();
        }
    }

    @Override
    public void updateToggleStatus(int studentId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Student student = session.get(Student.class, studentId);
            if (student != null) {
                student.setStatus(!student.isStatus());
                session.update(student);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Student> searchStudent(String keyword, int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "from Student where name like lower(concat('%', :keyword, '%')) or email like lower(concat('%', :keyword, '%'))";
            Integer id = null;

            try {
                id = Integer.parseInt(keyword);
                hql += " or studentId = :id";
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            Query<Student> query = session.createQuery(hql, Student.class);
            query.setParameter("keyword", keyword);

            if (id != null) {
                query.setParameter("id", id);
            }

            if (page < 1) page = 1;

            query.setFirstResult((page - 1) *  size);
            query.setMaxResults(size);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public long countStudent(String keyword) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "select count(*) from Student where name like lower(concat('%', :keyword, '%')) or email like lower(concat('%', :keyword, '%'))";

            Integer id = null;

            try {
                id = Integer.parseInt(keyword);
                hql += " or studentId = :id";
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", keyword);

            if (id != null) {
                query.setParameter("id", id);
            }

            return query.getSingleResult();
        }finally {
            session.close();
        }
    }

    @Override
    public List<Student> sortStudentById(String sortType, int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "from Student";

            if (sortType.equals("asc")){
                hql += " order by studentId";
            } else if (sortType.equals("desc")) {
                hql += " order by studentId desc";
            }

            Query<Student> query = session.createQuery(hql, Student.class);
            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public List<Student> sortStudentByName(String sortType, int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "from Student";

            if (sortType.equals("asc")){
                hql += " order by name";
            } else if (sortType.equals("desc")) {
                hql += " order by name desc";
            }

            Query<Student> query = session.createQuery(hql, Student.class);
            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public List<CourseStats> totalStudentOfCourse() {
        Session session = sessionFactory.openSession();

        String hql = "select new com.data.model.CourseStats(" +
                "c.courseId, c.courseName, c.image, c.duration, count(e.student.studentId)) " +
                "from com.data.model.Course c " +
                "left join com.data.model.Enrollment e on c.courseId = e.course.courseId and e.status = 'CONFIRMED' " +
                "group by c.courseId, c.courseName, c.instructor, c.duration " +
                "order by count(e.student.studentId) desc";

        Query<CourseStats> query = session.createQuery(hql, CourseStats.class);
        List<CourseStats> result = query.getResultList();

        session.close();
        return result;
    }

    @Override
    public void update(Student student) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.update(student);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public Student findById(int studentId) {
        Session session = sessionFactory.openSession();

        try {
            Query<Student> query = session.createQuery("from Student where studentId = :studentId", Student.class);
            query.setParameter("studentId", studentId);

            return query.getSingleResult();
        }finally {
            session.close();
        }
    }

    @Override
    public Student findByEmail(String email) {
        Session session = sessionFactory.openSession();

        try {
            Query<Student> query = session.createQuery("from Student where email = :email", Student.class);
            query.setParameter("email", email);
            List<Student> results = query.getResultList();

            return results.isEmpty() ? null : results.get(0);
        }finally {
            session.close();
        }
    }

    @Override
    public Student findByPhone(String phone) {
        Session session = sessionFactory.openSession();

        try {
            Query<Student> query = session.createQuery("from Student where phone = :phone", Student.class);
            query.setParameter("phone", phone);
            List<Student> results = query.getResultList();

            return results.isEmpty() ? null : results.get(0);
        }finally {
            session.close();
        }
    }

    @Override
    public void changePassWord(int studentId, String passWord) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Student student = findById(studentId);
            if (student != null) {
                student.setPassword(passWord);
                session.update(student);
                transaction.commit();
            }
        }finally {
            session.close();
        }
    }
}
