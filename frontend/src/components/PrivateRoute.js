import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const PrivateRoute = ({ children, allowedRoles }) => {
    const { user, loading, isAuthenticated } = useAuth();
    const location = useLocation();

    if (loading) {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh'
            }}>
                <div className="loader"></div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
        return (
            <div style={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                height: '80vh',
                textAlign: 'center',
                padding: '20px'
            }}>
                <h2 style={{ color: 'var(--error)', marginBottom: '16px' }}>Accès Refusé</h2>
                <p style={{ color: 'var(--gray-400)' }}>
                    Vous n'avez pas le droit d'accéder à cette fonctionnalité.
                    <br />
                    Veuillez contacter votre administrateur.
                </p>
            </div>
        );
    }

    return children;
};

export default PrivateRoute;
