import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Forms.css';

const ChangePassword = () => {
    const [formData, setFormData] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const { changePassword, logout } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (formData.newPassword !== formData.confirmPassword) {
            setError('Les mots de passe ne correspondent pas');
            return;
        }

        if (formData.newPassword.length < 6) {
            setError('Le nouveau mot de passe doit contenir au moins 6 caractères');
            return;
        }

        setLoading(true);

        try {
            await changePassword(
                formData.currentPassword,
                formData.newPassword,
                formData.confirmPassword
            );
            setSuccess('Mot de passe changé avec succès. Vous allez être déconnecté...');

            setTimeout(() => {
                logout();
                navigate('/login', { state: { message: 'Mot de passe changé. Veuillez vous reconnecter.' } });
            }, 2000);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="form-page">
            <div className="form-container animate-slideUp" style={{ maxWidth: '480px' }}>
                <div className="form-header">
                    <h1 className="form-title">Changer le Mot de Passe</h1>
                    <p className="form-subtitle">Mettez à jour votre mot de passe</p>
                </div>

                {error && <div className="alert alert-error">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Mot de passe actuel *</label>
                        <input
                            type="password"
                            name="currentPassword"
                            className="form-input"
                            placeholder="Entrez votre mot de passe actuel"
                            value={formData.currentPassword}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Nouveau mot de passe *</label>
                        <input
                            type="password"
                            name="newPassword"
                            className="form-input"
                            placeholder="Entrez votre nouveau mot de passe"
                            value={formData.newPassword}
                            onChange={handleChange}
                            required
                            minLength={6}
                        />
                        <span className="form-hint">Minimum 6 caractères</span>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Confirmer le mot de passe *</label>
                        <input
                            type="password"
                            name="confirmPassword"
                            className="form-input"
                            placeholder="Confirmez votre nouveau mot de passe"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary submit-btn"
                        disabled={loading}
                    >
                        {loading ? 'Changement en cours...' : 'Changer le Mot de Passe'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ChangePassword;
