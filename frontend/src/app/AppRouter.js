import { Navigate, Route, Routes } from 'react-router-dom';
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import AdminLoginPage from '../pages/admin/AdminLoginPage';
import HomePage from '../pages/user/HomePage';
import ExamPage from '../pages/user/ExamPage';
import ResultPage from '../pages/user/ResultPage';
import UserLayout from '../layouts/UserLayout';
import AdminLayout from '../layouts/AdminLayout';
import AdminDashboardPage from '../pages/admin/AdminDashboardPage';
import AdminExamsPage from '../pages/admin/AdminExamsPage';
import AdminExamEditorPage from '../pages/admin/AdminExamEditorPage';
import AdminUsersPage from '../pages/admin/AdminUsersPage';
import AdminStatisticsPage from '../pages/admin/AdminStatisticsPage';
import AdminStudentResultsPage from '../pages/admin/AdminStudentResultsPage';
import RequireAdmin from '../routes/RequireAdmin';
import RequireUser from '../routes/RequireUser';

export default function AppRouter() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route path="/admin/login" element={<AdminLoginPage />} />

            <Route
                path="/"
                element={
                    <RequireUser>
                        <UserLayout />
                    </RequireUser>
                }
            >
                <Route index element={<HomePage />} />
                <Route path="exams/:examId" element={<ExamPage />} />
                <Route path="results/:attemptId" element={<ResultPage />} />
            </Route>

            <Route
                path="/admin"
                element={
                    <RequireAdmin>
                        <AdminLayout />
                    </RequireAdmin>
                }
            >
                <Route index element={<AdminDashboardPage />} />
                <Route path="exams" element={<AdminExamsPage />} />
                <Route path="exams/new" element={<AdminExamEditorPage mode="create" />} />
                <Route path="exams/:examId/edit" element={<AdminExamEditorPage mode="edit" />} />
                <Route path="users" element={<AdminUsersPage />} />
                <Route path="statistics" element={<AdminStatisticsPage />} />
                <Route path="students" element={<AdminStudentResultsPage />} />
            </Route>

            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
}
