import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TimestampedAnswer } from '../entities/answer-dto';

interface TimeZone {
  label: string;
  correctAnswers: number;
  wrongAnswers: number;
}

@Component({
  selector: 'app-wisdom-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './wisdom-chart.component.html',
  styleUrl: './wisdom-chart.component.css'
})
export class WisdomChartComponent implements OnChanges {
  @Input() answers?: TimestampedAnswer[];
  @Input() dashboardId?: string;

  timeZones: TimeZone[] = [];
  maxCount = 0;
  showNoData = false;
  Math = Math;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['answers'] || changes['dashboardId']) {
      this.processAnswers();
    }
  }

  private processAnswers(): void {
    // Generate dummy data for session-001 if no answers received
    if (this.dashboardId === 'session-001' && (!this.answers || this.answers.length === 0)) {
      this.generateDummyData();
      return;
    }

    if (!this.answers || this.answers.length === 0) {
      this.showNoData = true;
      this.timeZones = [];
      return;
    }

    this.showNoData = false;
    this.calculateTimeZones();
  }

  private generateDummyData(): void {
    const now = new Date();
    const dummyAnswers: TimestampedAnswer[] = [];

    // Generate 40 answers spread over 2 hours
    for (let i = 0; i < 40; i++) {
      const timestamp = new Date(now.getTime() - (120 - i * 3) * 60000); // Spread over 2 hours
      dummyAnswers.push({
        item1: timestamp.toISOString(),
        item2: {
          questionId: `q-${i}`,
          selectedOptionId: `opt-${i}`,
          isCorrect: Math.random() > 0.4 // 60% correct rate
        }
      });
    }

    this.answers = dummyAnswers;
    this.calculateTimeZones();
  }

  private calculateTimeZones(): void {
    if (!this.answers || this.answers.length === 0) {
      return;
    }

    const timestamps = this.answers.map(a => new Date(a.item1).getTime());
    const minTime = Math.min(...timestamps);
    const maxTime = Math.max(...timestamps);
    const timeRange = maxTime - minTime;
    const zoneSize = timeRange / 5;

    this.timeZones = [];

    for (let i = 0; i < 5; i++) {
      const zoneStart = minTime + (i * zoneSize);
      const zoneEnd = minTime + ((i + 1) * zoneSize);

      const answersInZone = this.answers.filter(a => {
        const time = new Date(a.item1).getTime();
        return time >= zoneStart && time < zoneEnd;
      });

      const correctCount = answersInZone.filter(a => a.item2.isCorrect).length;
      const wrongCount = answersInZone.length - correctCount;

      this.timeZones.push({
        label: this.formatTimeZoneLabel(new Date(zoneStart), new Date(zoneEnd)),
        correctAnswers: correctCount,
        wrongAnswers: wrongCount
      });
    }

    this.maxCount = Math.max(
      ...this.timeZones.map(z => z.correctAnswers + z.wrongAnswers),
      1
    );
  }

  private formatTimeZoneLabel(start: Date, end: Date): string {
    return `${this.formatTime(start)} - ${this.formatTime(end)}`;
  }

  private formatTime(date: Date): string {
    return date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });
  }

  getBarHeight(count: number): number {
    return (count / this.maxCount) * 100;
  }
}
