# Security Policy

## Supported Versions

We release patches for security vulnerabilities. Currently supported versions:

| Version | Supported          |
| ------- | ------------------ |
| 0.0.x   | :white_check_mark: |

## Reporting a Vulnerability

The AI Agent team takes security bugs seriously. We appreciate your efforts to responsibly disclose your findings.

### How to Report a Security Vulnerability?

**Please do not report security vulnerabilities through public GitHub issues.**

Instead, please report them via GitHub's security advisory feature:

1. Go to the repository's Security tab
2. Click "Report a vulnerability"
3. Fill out the form with details about the vulnerability

Alternatively, you can email the maintainers directly through GitHub.

### What to Include in Your Report

Please include the following information:

* Type of issue (e.g., buffer overflow, SQL injection, cross-site scripting, etc.)
* Full paths of source file(s) related to the manifestation of the issue
* The location of the affected source code (tag/branch/commit or direct URL)
* Any special configuration required to reproduce the issue
* Step-by-step instructions to reproduce the issue
* Proof-of-concept or exploit code (if possible)
* Impact of the issue, including how an attacker might exploit it

This information will help us triage your report more quickly.

### What to Expect

* We will acknowledge receipt of your vulnerability report within 3 business days
* We will send a more detailed response within 7 days indicating the next steps
* We will work with you to understand and validate the issue
* We will notify you when the issue is fixed
* We will credit you in the security advisory (if you wish)

## Security Best Practices

When using this project:

1. **Never commit sensitive data**: Don't include API keys, passwords, or other secrets in your code
2. **Use environment variables**: Store configuration and secrets in environment variables
3. **Keep dependencies updated**: Regularly update dependencies to get security patches
4. **Database security**: Use strong passwords and restrict database access
5. **API security**: Implement proper authentication and authorization
6. **Input validation**: Always validate and sanitize user inputs

## Known Security Considerations

* This project connects to external AI services - ensure API keys are kept secure
* Database credentials should be stored securely and never committed to version control
* When using the YouTube document reader, be aware of potential content security issues
* Vector store data may contain sensitive information - implement appropriate access controls

## Security Updates

Security updates will be released as soon as possible after a vulnerability is confirmed. Please watch this repository for security updates and upgrade as soon as updates are available.

## Comments on This Policy

If you have suggestions on how this process could be improved, please submit a pull request or open an issue.
