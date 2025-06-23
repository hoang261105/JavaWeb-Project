package com.data.repository.enrollment;

import com.data.model.Course;
import com.data.model.Enrollment;
import com.data.model.Status;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EnrollmentRepositoryImp implements EnrollmentRepository{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void registerCourse(Enrollment enrollment) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.save(enrollment);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public long count() {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(*) from Enrollment where status = 'CONFIRMED'", Long.class);

            return query.getSingleResult();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Enrollment> findAll() {
        Session session = sessionFactory.openSession();

        try {
            Query<Enrollment> query = session.createQuery("from Enrollment", Enrollment.class);

            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public List<Enrollment> findByStudentId(int studentId) {
        Session session = sessionFactory.openSession();

        try {
            Query<Enrollment> query = session.createQuery("from Enrollment e where e.student.studentId = :studentId", Enrollment.class);

            query.setParameter("studentId", studentId);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public List<Enrollment> findAllEnrollmentById(int studentId, int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            Query<Enrollment> query = session.createQuery("from Enrollment e where e.student.studentId = :studentId order by e.registeredAt desc", Enrollment.class);
            query.setParameter("studentId", studentId);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public long countAllEnrollment(int studentId) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(e) from Enrollment e where e.student.studentId = :studentId", Long.class);

            query.setParameter("studentId", studentId);
            return query.getSingleResult();
        }finally {
            session.close();
        }
    }

    @Override
    public List<Enrollment> searchCourseByStudentId(int studentId, String courseName, int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            Query<Enrollment> query = session.createQuery("from Enrollment e where e.student.studentId = :studentId and e.course.courseName like concat('%', :courseName, '%')", Enrollment.class);

            query.setParameter("studentId", studentId);
            query.setParameter("courseName", courseName);
            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public long countCourseByStudentId(int studentId, String courseName) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(e) from Enrollment e where e.student.studentId = :studentId and e.course.courseName like concat('%', :courseName, '%') order by e.registeredAt desc", Long.class);
            query.setParameter("studentId", studentId);
            query.setParameter("courseName", courseName);
            return query.getSingleResult();
        }finally {
            session.close();
        }
    }

    @Override
    public List<Enrollment> sortEnrollmentByStatus(int studentId, String sortType, int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "from Enrollment e where e.student.studentId = :studentId";
            if (sortType.equals("asc")){
                hql += " order by e.status";
            }else if (sortType.equals("desc")){
                hql += " order by e.status desc";
            }

            Query<Enrollment> query = session.createQuery(hql, Enrollment.class);
            query.setParameter("studentId", studentId);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public long countEnrollmentByStatus(int studentId, String status) {
        Session session = sessionFactory.openSession();

        try {
            Status enumStatus = Status.valueOf(status);

            Query<Long> query = session.createQuery("select count(e) from Enrollment e where e.student.studentId = :studentId and e.status = :status", Long.class);
            query.setParameter("studentId", studentId);
            query.setParameter("status", enumStatus);
            return query.getSingleResult();
        }finally {
            session.close();
        }
    }

    @Override
    public List<Enrollment> getAllEnrollments(int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            Query<Enrollment> query = session.createQuery("from Enrollment order by registeredAt desc", Enrollment.class);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public void approval(int id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Enrollment enrollment = session.get(Enrollment.class, id);
            if (enrollment != null){
                enrollment.setStatus(Status.CONFIRMED);
                session.update(enrollment);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void deny(int id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Enrollment enrollment = session.get(Enrollment.class, id);
            if (enrollment != null){
                enrollment.setStatus(Status.DENIED);
                session.update(enrollment);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void cancel(int id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Enrollment enrollment = session.get(Enrollment.class, id);
            if (enrollment != null){
                enrollment.setStatus(Status.CANCELLED);
                session.update(enrollment);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Enrollment> searchCourse(String name, int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            Query<Enrollment> query = session.createQuery("from Enrollment e where e.course.courseName like concat('%', :name, '%') order by e.registeredAt desc", Enrollment.class);
            query.setParameter("name", name);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public long countCourseSearch(String name) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(e) from Enrollment e where e.course.courseName like concat('%', :name, '%')", Long.class);
            query.setParameter("name", name);

            return query.getSingleResult();
        }finally {
            session.close();
        }
    }

    @Override
    public List<Enrollment> filterByStatus(String status, int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            Status enumStatus = Status.valueOf(status);

            Query<Enrollment> query = session.createQuery("from Enrollment e where e.status = :status order by e.registeredAt desc", Enrollment.class);
            query.setParameter("status", enumStatus);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public long countByStatus(String status) {
        Session session = sessionFactory.openSession();

        try {
            Status enumStatus = Status.valueOf(status);

            Query<Long> query = session.createQuery("select count(e) from Enrollment e where e.status = :status", Long.class);
            query.setParameter("status", enumStatus);
            return query.getSingleResult();
        }finally {
            session.close();
        }
    }

}
