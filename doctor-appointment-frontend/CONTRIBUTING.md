# Contributing to Doctor Appointment Platform

Thank you for your interest in contributing! Here's how to get started.

## 🚀 Getting Started

1. Fork the repository.
2. Create your feature branch: `git checkout -b feature/your-feature-name`
3. Make your changes following our standards below.
4. Commit using conventional commits: `git commit -m "feat: add doctor rating system"`
5. Push your branch: `git push origin feature/your-feature-name`
6. Open a Pull Request.

## ✅ Code Standards

- **TypeScript**: All code must compile with zero errors (`npm run build`).
- **Linting**: All code must pass OxLint with zero warnings (`npm run lint`).
- **Naming**: Use `camelCase` for variables, `PascalCase` for components/types.
- **Architecture**: Follow the existing feature-based folder structure.
- **No mocks**: Never use mock data. Connect all features to real backend APIs.

## 📝 Commit Message Format

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add new feature
fix: fix a bug
docs: update documentation
style: formatting changes
refactor: code refactor without feature change
chore: maintenance tasks
```

## 📋 Pull Request Checklist

- [ ] Code compiles (`npm run build`)
- [ ] Linter passes (`npm run lint`)
- [ ] No mock data or TODO placeholders
- [ ] Follows feature-based folder structure
- [ ] All new components handle loading, empty, and error states
- [ ] Responsive design verified on mobile and desktop

## ❓ Questions?

Open a [GitHub Issue](https://github.com/your-username/doctor-appointment-platform/issues).
