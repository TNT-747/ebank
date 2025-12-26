import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Logo from './Logo';
import './Navbar.css';

const Navbar = () => {
    const { user, logout, isAgent, isClient } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [menuOpen, setMenuOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const isActive = (path) => location.pathname === path;

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-brand">
                    <Logo size="small" showText={true} />
                </Link>

                <button
                    className="navbar-toggle"
                    onClick={() => setMenuOpen(!menuOpen)}
                >
                    ☰
                </button>

                <div className={`navbar-menu ${menuOpen ? 'open' : ''}`}>
                    {isAgent() && (
                        <>
                            <Link
                                to="/new-client"
                                className={`nav-link ${isActive('/new-client') ? 'active' : ''}`}
                            >
                                Nouveau Client
                            </Link>
                            <Link
                                to="/new-account"
                                className={`nav-link ${isActive('/new-account') ? 'active' : ''}`}
                            >
                                Nouveau Compte
                            </Link>
                        </>
                    )}

                    {isClient() && (
                        <>
                            <Link
                                to="/dashboard"
                                className={`nav-link ${isActive('/dashboard') ? 'active' : ''}`}
                            >
                                Tableau de Bord
                            </Link>
                            <Link
                                to="/new-transfer"
                                className={`nav-link ${isActive('/new-transfer') ? 'active' : ''}`}
                            >
                                Nouveau Virement
                            </Link>
                        </>
                    )}

                    <Link
                        to="/change-password"
                        className={`nav-link ${isActive('/change-password') ? 'active' : ''}`}
                    >
                        Changer Mot de Passe
                    </Link>

                    <div className="nav-user">
                        <span className="user-info">
                            <span className="user-name">{user?.username}</span>
                            <span className="user-role">{user?.role === 'AGENT_GUICHET' ? 'Agent' : 'Client'}</span>
                        </span>
                        <button onClick={handleLogout} className="btn-logout">
                            Déconnexion
                        </button>
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
