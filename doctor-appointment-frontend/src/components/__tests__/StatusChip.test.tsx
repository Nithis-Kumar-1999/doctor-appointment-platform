import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import StatusChip from '../StatusChip';

describe('StatusChip Component', () => {
  it('renders SCHEDULED status correctly', () => {
    render(<StatusChip status="SCHEDULED" />);
    const chip = screen.getByText(/scheduled/i);
    expect(chip).toBeInTheDocument();
    expect(chip).toHaveClass('MuiChip-colorInfo');
  });

  it('renders COMPLETED status correctly', () => {
    render(<StatusChip status="COMPLETED" />);
    const chip = screen.getByText(/completed/i);
    expect(chip).toBeInTheDocument();
    expect(chip).toHaveClass('MuiChip-colorSuccess');
  });

  it('renders CANCELLED status correctly', () => {
    render(<StatusChip status="CANCELLED" />);
    const chip = screen.getByText(/cancelled/i);
    expect(chip).toBeInTheDocument();
    expect(chip).toHaveClass('MuiChip-colorError');
  });

  it('falls back to default color for unknown statuses', () => {
    render(<StatusChip status={'UNKNOWN' as any} />);
    const chip = screen.getByText(/unknown/i);
    expect(chip).toBeInTheDocument();
    expect(chip).toHaveClass('MuiChip-colorDefault');
  });
});
