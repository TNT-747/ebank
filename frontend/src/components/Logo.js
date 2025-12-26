import React from 'react';
import './Logo.css';

const Logo = ({ size = 'medium', showText = true }) => {
    const sizeClass = `logo-${size}`;

    return (
        <div className={`bk-logo ${sizeClass}`}>
            <div className="logo-symbol">
                <div className="shield-outer">
                    <div className="shield-inner">
                        <div className="vault-door">
                            <div className="vault-handle"></div>
                        </div>
                        <div className="growth-arrow">
                            <div className="arrow-stem"></div>
                            <div className="arrow-head"></div>
                        </div>
                    </div>
                </div>
            </div>
            {showText && (
                <div className="logo-text">
                    <span className="bank-name">
                        <span className="name-primary">Boussouga</span>
                        <span className="name-separator">&</span>
                        <span className="name-primary">Kassimi</span>
                    </span>
                    <span className="bank-tagline">BANK</span>
                </div>
            )}
        </div>
    );
};

export default Logo;
