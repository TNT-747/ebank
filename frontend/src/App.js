import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import NewClient from './pages/NewClient';
import NewAccount from './pages/NewAccount';
import NewTransfer from './pages/NewTransfer';
import ChangePassword from './pages/ChangePassword';

// Layout component with Navbar
const Layout = ({ children }) => {
    return (
        <>
            <Navbar />
            <main className="container">
                {children}
            </main>
        </>
    );
};

// Home redirect based on role
const HomeRedirect = () => {
    const { user, isAgent, isClient } = useAuth();

    if (!user) {
        return <Navigate to="/login" />;
    }

    if (isAgent()) {
        return <Navigate to="/new-client" />;
    }

    if (isClient()) {
        return <Navigate to="/dashboard" />;
    }

    return <Navigate to="/login" />;
};

function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    {/* Public routes */}
                    <Route path="/login" element={<Login />} />

                    {/* Home redirect */}
                    <Route path="/" element={<HomeRedirect />} />

                    {/* Protected routes for AGENT_GUICHET */}
                    <Route
                        path="/new-client"
                        element={
                            <PrivateRoute allowedRoles={['AGENT_GUICHET']}>
                                <Layout>
                                    <NewClient />
                                </Layout>
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/new-account"
                        element={
                            <PrivateRoute allowedRoles={['AGENT_GUICHET']}>
                                <Layout>
                                    <NewAccount />
                                </Layout>
                            </PrivateRoute>
                        }
                    />

                    {/* Protected routes for CLIENT */}
                    <Route
                        path="/dashboard"
                        element={
                            <PrivateRoute allowedRoles={['CLIENT']}>
                                <Layout>
                                    <Dashboard />
                                </Layout>
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/new-transfer"
                        element={
                            <PrivateRoute allowedRoles={['CLIENT']}>
                                <Layout>
                                    <NewTransfer />
                                </Layout>
                            </PrivateRoute>
                        }
                    />

                    {/* Change password - accessible to all authenticated users */}
                    <Route
                        path="/change-password"
                        element={
                            <PrivateRoute allowedRoles={['CLIENT', 'AGENT_GUICHET']}>
                                <Layout>
                                    <ChangePassword />
                                </Layout>
                            </PrivateRoute>
                        }
                    />

                    {/* Catch all - redirect to home */}
                    <Route path="*" element={<Navigate to="/" />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;
