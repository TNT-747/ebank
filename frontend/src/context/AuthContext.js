import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check for existing session
        const token = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');

        if (token && storedUser) {
            try {
                setUser(JSON.parse(storedUser));
            } catch (e) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
            }
        }
        setLoading(false);
    }, []);

    const login = async (username, password) => {
        const response = await authAPI.login({ username, password });
        const { data } = response.data;

        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify({
            username: data.username,
            role: data.role,
            email: data.email,
        }));

        setUser({
            username: data.username,
            role: data.role,
            email: data.email,
        });

        return data;
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
    };

    const changePassword = async (currentPassword, newPassword, confirmPassword) => {
        await authAPI.changePassword({
            currentPassword,
            newPassword,
            confirmPassword,
        });
    };

    const isAgent = () => user?.role === 'AGENT_GUICHET';
    const isClient = () => user?.role === 'CLIENT';

    const value = {
        user,
        loading,
        login,
        logout,
        changePassword,
        isAgent,
        isClient,
        isAuthenticated: !!user,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
