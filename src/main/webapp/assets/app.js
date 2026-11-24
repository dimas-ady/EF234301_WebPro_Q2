(function () {
    'use strict';

    var csrfMeta = document.querySelector('meta[name="csrf-token"]');
    var csrfToken = csrfMeta ? csrfMeta.getAttribute('content') : '';

    function requestHeaders() {
        return {
            'X-CSRF-TOKEN': csrfToken,
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };
    }

    function copyText(value) {
        if (navigator.clipboard && typeof navigator.clipboard.writeText === 'function') {
            return navigator.clipboard.writeText(value).then(function () {
                return true;
            }).catch(function () {
                return false;
            });
        }
        try {
            var helper = document.createElement('textarea');
            helper.value = value;
            helper.setAttribute('readonly', '');
            helper.style.position = 'fixed';
            helper.style.opacity = '0';
            document.body.appendChild(helper);
            helper.select();
            var copied = document.execCommand('copy');
            document.body.removeChild(helper);
            return Promise.resolve(copied);
        } catch (error) {
            return Promise.resolve(false);
        }
    }

    function flashLabel(button, label) {
        var original = button.getAttribute('data-label') || button.textContent;
        button.setAttribute('data-label', original);
        button.textContent = label;
        window.setTimeout(function () {
            button.textContent = button.getAttribute('data-label');
        }, 1500);
    }

    function syncFavorite(button, favorite) {
        button.classList.toggle('bg-amber-50', favorite);
        button.classList.toggle('text-amber-600', favorite);
        button.classList.toggle('bg-stone-50', !favorite);
        button.classList.toggle('text-stone-500', !favorite);
        button.classList.toggle('hover:text-amber-600', !favorite);
        button.setAttribute('aria-pressed', String(favorite));
        button.setAttribute('aria-label', favorite ? 'Remove from favorites' : 'Add to favorites');
        var icon = button.querySelector('[aria-hidden="true"]');
        var label = button.querySelector('.favorite-label');
        if (icon) icon.textContent = favorite ? '★' : '☆';
        if (label) label.textContent = favorite ? 'Favorite' : 'Add favorite';
    }

    function credentialMessage(button, message) {
        var card = button.closest('[data-credential-card]');
        var target = card ? card.querySelector('[data-credential-message]') : null;
        if (target) {
            target.textContent = message;
        }
    }

    /* Sidebar */

    var body = document.body;
    var backdrop = document.getElementById('sidebar-backdrop');

    function syncSidebar(open) {
        document.querySelectorAll('[data-sidebar-toggle]').forEach(function (button) {
            button.setAttribute('aria-expanded', String(open));
        });
    }

    function openSidebar() {
        body.classList.add('sidebar-open');
        if (backdrop) {
            backdrop.hidden = false;
            backdrop.classList.remove('hidden');
        }
        syncSidebar(true);
    }

    function closeSidebar() {
        body.classList.remove('sidebar-open');
        if (backdrop) {
            backdrop.hidden = true;
            backdrop.classList.add('hidden');
        }
        syncSidebar(false);
    }

    document.querySelectorAll('[data-sidebar-toggle]').forEach(function (button) {
        button.addEventListener('click', openSidebar);
    });

    document.querySelectorAll('[data-sidebar-close]').forEach(function (button) {
        button.addEventListener('click', closeSidebar);
    });

    if (backdrop) {
        backdrop.addEventListener('click', closeSidebar);
    }

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            closeSidebar();
        }
    });

    /* Confirm destructive forms */

    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!window.confirm(form.getAttribute('data-confirm'))) {
                event.preventDefault();
            }
        });
    });

    /* Favorites */

    document.querySelectorAll('[data-favorite-url]').forEach(function (button) {
        button.addEventListener('click', function () {
            button.disabled = true;
            button.setAttribute('aria-busy', 'true');
            var label = button.querySelector('.favorite-label');
            var previousLabel = label ? label.textContent : button.textContent;
            if (label) label.textContent = 'Saving...';
            fetch(button.getAttribute('data-favorite-url'), {
                method: 'PATCH',
                headers: requestHeaders(),
                credentials: 'same-origin'
            }).then(function (response) {
                if (response.ok) {
                    syncFavorite(button, button.getAttribute('aria-pressed') !== 'true');
                    button.disabled = false;
                    button.removeAttribute('aria-busy');
                    credentialMessage(button, '');
                    return;
                }
                button.disabled = false;
                button.removeAttribute('aria-busy');
                if (label) label.textContent = previousLabel;
                credentialMessage(button, 'Favorite could not be updated. Please try again.');
            }).catch(function () {
                button.disabled = false;
                button.removeAttribute('aria-busy');
                if (label) label.textContent = previousLabel;
                credentialMessage(button, 'Favorite could not be updated. Check your connection and try again.');
            });
        });
    });

    /* Password reveal and copy */

    function fetchPassword(url) {
        return fetch(url, {
            method: 'POST',
            headers: requestHeaders(),
            credentials: 'same-origin'
        }).then(function (response) {
            if (!response.ok) {
                throw new Error('Request failed');
            }
            return response.json();
        }).then(function (data) {
            return typeof data.password === 'string' ? data.password : '';
        });
    }

    document.querySelectorAll('[data-password-action]').forEach(function (button) {
        button.addEventListener('click', function () {
            var target = document.getElementById(button.getAttribute('data-password-target'));
            if (!target) {
                return;
            }
            var action = button.getAttribute('data-password-action');

            if (action === 'reveal') {
                if (target.getAttribute('data-revealed') === 'true') {
                    target.textContent = '\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022';
                    target.setAttribute('data-revealed', 'false');
                    button.textContent = 'Reveal';
                    return;
                }
                button.disabled = true;
                button.setAttribute('aria-busy', 'true');
                var previousLabel = button.textContent;
                button.textContent = 'Loading...';
                fetchPassword(button.getAttribute('data-password-url')).then(function (password) {
                    target.textContent = password;
                    target.setAttribute('data-revealed', 'true');
                    target.setAttribute('data-password', password);
                    button.textContent = 'Hide';
                }).catch(function () {
                    button.textContent = previousLabel;
                    credentialMessage(button, 'Password could not be revealed. Please try again.');
                }).then(function () {
                    button.disabled = false;
                    button.removeAttribute('aria-busy');
                });
                return;
            }

            button.disabled = true;
            button.setAttribute('aria-busy', 'true');
            var cached = target.getAttribute('data-password');
            var pending = cached !== null
                ? Promise.resolve(cached)
                : fetchPassword(button.getAttribute('data-password-url'));

            pending.then(function (password) {
                target.setAttribute('data-password', password);
                return copyText(password);
            }).then(function (copied) {
                flashLabel(button, copied ? 'Copied' : 'Copy failed');
            }).catch(function () {
                flashLabel(button, 'Copy failed');
            }).then(function () {
                button.disabled = false;
                button.removeAttribute('aria-busy');
            });
        });
    });

    /* Copy username */

    document.querySelectorAll('[data-copy-value]').forEach(function (button) {
        button.addEventListener('click', function () {
            copyText(button.getAttribute('data-copy-value')).then(function (copied) {
                flashLabel(button, copied ? 'Copied' : 'Copy failed');
            });
        });
    });

    /* Password generator */

    var generator = document.querySelector('[data-password-generator]');
    if (generator && generator.getAttribute('data-initialized') !== 'true') {
        generator.setAttribute('data-initialized', 'true');

        var sets = {
            uppercase: 'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
            lowercase: 'abcdefghijklmnopqrstuvwxyz',
            numbers: '0123456789',
            symbols: '!@#$%^&*()-_=+[]{}:,.?'
        };

        var lengthInput = generator.querySelector('#length');
        var lengthValue = generator.querySelector('#length-value');
        var output = generator.querySelector('#generated-password');
        var status = generator.querySelector('#generator-status');
        var generateButton = generator.querySelector('#generate-password');
        var copyButton = generator.querySelector('#copy-password');

        function setStatus(message) {
            if (status) {
                status.textContent = message;
            }
        }

        function secureRandomIndex(max) {
            var limit = Math.floor(4294967296 / max) * max;
            var buffer = new Uint32Array(1);
            var value;
            do {
                window.crypto.getRandomValues(buffer);
                value = buffer[0];
            } while (value >= limit);
            return value % max;
        }

        function selectedSets() {
            return Array.prototype.slice.call(generator.querySelectorAll('input[data-set]'))
                .filter(function (input) {
                    return input.checked;
                })
                .map(function (input) {
                    return sets[input.value] || '';
                })
                .filter(function (pool) {
                    return pool.length > 0;
                });
        }

        function generate() {
            var length = Math.min(64, Math.max(8, parseInt(lengthInput.value, 10) || 16));
            var pools = selectedSets();

            if (!pools.length) {
                setStatus('Select at least one character set.');
                return;
            }
            if (!window.crypto || typeof window.crypto.getRandomValues !== 'function') {
                setStatus('This browser does not support secure random generation.');
                return;
            }

            var combined = pools.join('');
            var characters = pools.map(function (pool) {
                return pool.charAt(secureRandomIndex(pool.length));
            });

            while (characters.length < length) {
                characters.push(combined.charAt(secureRandomIndex(combined.length)));
            }

            for (var i = characters.length - 1; i > 0; i--) {
                var j = secureRandomIndex(i + 1);
                var swap = characters[i];
                characters[i] = characters[j];
                characters[j] = swap;
            }

            output.value = characters.join('');
            setStatus('New password generated.');
        }

        if (lengthInput) {
            lengthInput.addEventListener('input', function () {
                if (lengthValue) {
                    lengthValue.textContent = lengthInput.value + ' characters';
                }
            });
        }

        if (generateButton) {
            generateButton.addEventListener('click', generate);
        }

        if (copyButton) {
            copyButton.addEventListener('click', function () {
                if (!output.value) {
                    setStatus('No password is available to copy.');
                    return;
                }
                copyText(output.value).then(function (copied) {
                    setStatus(copied
                        ? 'Password copied.'
                        : 'Password could not be copied. Check browser clipboard permissions.');
                });
            });
        }

        generate();
    }
})();
