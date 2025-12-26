import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Logo from '../components/Logo';
import './Login.css';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const data = await login(username, password);

            // Redirect based on role
            if (data.role === 'AGENT_GUICHET') {
                navigate('/new-client');
            } else {
                navigate('/dashboard');
            }
        } catch (err) {
            setError(err.message || 'Login ou mot de passe erronés');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-background">
                <div className="bg-circle bg-circle-1"></div>
                <div className="bg-circle bg-circle-2"></div>
                <div className="bg-circle bg-circle-3"></div>
            </div>

            <div className="login-card animate-slideUp">
                <div className="login-header">
                    <Logo size="large" showText={true} />
                    <p className="login-subtitle">Votre partenaire bancaire de confiance</p>
                </div>

                {location.state?.message && (
                    <div className="alert alert-success">{location.state.message}</div>
                )}

                {error && <div className="alert alert-error">{error}</div>}

                <form onSubmit={handleSubmit} className="login-form">
                    <div className="form-group">
                        <label className="form-label">Nom d'utilisateur</label>
                        <input
                            type="text"
                            className="form-input"
                            placeholder="Entrez votre identifiant"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            autoComplete="username"
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Mot de passe</label>
                        <input
                            type="password"
                            className="form-input"
                            placeholder="Entrez votre mot de passe"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            autoComplete="current-password"
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary login-btn"
                        disabled={loading}
                    >
                        {loading ? (
                            <>
                                <span className="loader" style={{ width: 20, height: 20, borderWidth: 2 }}></span>
                                Connexion...
                            </>
                        ) : (
                            'Se connecter'
                        )}
                    </button>
                </form>

                <div className="login-footer">
                    <p className="demo-credentials">
                        <strong>Comptes de démonstration:</strong><br />
                        Agent: <code>agent / agent123</code><br />
                        Client: <code>client1 / client123</code>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;
