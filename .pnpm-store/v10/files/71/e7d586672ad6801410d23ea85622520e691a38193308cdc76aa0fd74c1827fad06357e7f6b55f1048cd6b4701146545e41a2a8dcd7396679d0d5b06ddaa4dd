import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy, ViewEncapsulation, Inject, LOCALE_ID, Optional, } from '@angular/core';
import { CountdownStatus } from './interfaces';
import { CountdownTimer } from './countdown.timer';
import { NgTemplateOutlet, formatDate } from '@angular/common';
import { COUNTDOWN_CONFIG } from './provide';
import * as i0 from "@angular/core";
import * as i1 from "./countdown.timer";
export class CountdownComponent {
    set config(i) {
        if (i.notify != null && !Array.isArray(i.notify) && i.notify > 0) {
            i.notify = [i.notify];
        }
        this._config = i;
    }
    get config() {
        return this._config;
    }
    constructor(locale, timer, cdr, ngZone, defCog) {
        this.locale = locale;
        this.timer = timer;
        this.cdr = cdr;
        this.ngZone = ngZone;
        this.defCog = defCog;
        this.frequency = 1000;
        this._notify = {};
        this.status = CountdownStatus.ing;
        this.isDestroy = false;
        this.i = {};
        this.left = 0;
        this.event = new EventEmitter();
    }
    /**
     * Start countdown, you must manually call when `demand: false`
     */
    begin() {
        this.status = CountdownStatus.ing;
        this.callEvent('start');
    }
    /**
     * Restart countdown
     */
    restart() {
        if (this.status !== CountdownStatus.stop) {
            this.destroy();
        }
        this.init();
        this.callEvent('restart');
    }
    /**
     * Stop countdown, must call `restart` when stopped, it's different from pause, unable to recover
     */
    stop() {
        if (this.status === CountdownStatus.stop) {
            return;
        }
        this.status = CountdownStatus.stop;
        this.destroy();
        this.callEvent('stop');
    }
    /**
     * Pause countdown, you can use `resume` to recover again
     */
    pause() {
        if (this.status === CountdownStatus.stop || this.status === CountdownStatus.pause) {
            return;
        }
        this.status = CountdownStatus.pause;
        this.callEvent('pause');
    }
    /**
     * Resume countdown
     */
    resume() {
        if (this.status === CountdownStatus.stop || this.status !== CountdownStatus.pause) {
            return;
        }
        this.status = CountdownStatus.ing;
        this.callEvent('resume');
    }
    callEvent(action) {
        this.event.emit({ action, left: this.left, status: this.status, text: this.i.text });
    }
    init() {
        const config = (this.config = {
            demand: false,
            leftTime: 0,
            format: 'HH:mm:ss',
            timezone: '+0000',
            formatDate: ({ date, formatStr, timezone }) => {
                return formatDate(new Date(date), formatStr, this.locale, timezone || '+0000');
            },
            ...this.defCog,
            ...this.config,
        });
        const frq = (this.frequency = ~config.format.indexOf('S') ? 100 : 1000);
        this.status = config.demand ? CountdownStatus.pause : CountdownStatus.ing;
        this.getLeft();
        // bind reflow to me
        const _reflow = this.reflow;
        this.reflow = (count = 0, force = false) => _reflow.apply(this, [count, force]);
        if (Array.isArray(config.notify)) {
            config.notify.forEach((time) => {
                if (time < 1) {
                    throw new Error(`The notify config must be a positive integer.`);
                }
                time = time * 1000;
                time = time - (time % frq);
                this._notify[time] = true;
            });
        }
        this.timer.add(this.reflow, frq).start();
        this.reflow(0, true);
    }
    destroy() {
        this.timer.remove(this.reflow);
        return this;
    }
    /**
     * 更新时钟
     */
    reflow(count = 0, force = false) {
        if (this.isDestroy) {
            return;
        }
        const { status, config, _notify } = this;
        if (!force && status !== CountdownStatus.ing) {
            return;
        }
        let value = (this.left = this.left - this.frequency * count);
        if (value < 1) {
            value = 0;
        }
        this.i = {
            value,
            text: config.formatDate({ date: value, formatStr: config.format, timezone: config.timezone }),
        };
        if (typeof config.prettyText === 'function') {
            this.i.text = config.prettyText(this.i.text);
        }
        this.cdr.detectChanges();
        if (config.notify === 0 || _notify[value]) {
            this.ngZone.run(() => {
                this.callEvent('notify');
            });
        }
        if (value === 0) {
            this.ngZone.run(() => {
                this.status = CountdownStatus.done;
                this.destroy();
                this.callEvent('done');
            });
        }
    }
    /**
     * 获取倒计时剩余帧数
     */
    getLeft() {
        const { config, frequency } = this;
        let left = config.leftTime * 1000;
        const end = config.stopTime;
        if (!left && end) {
            left = end - new Date().getTime();
        }
        this.left = left - (left % frequency);
    }
    ngOnInit() {
        this.init();
        if (!this.config.demand) {
            this.begin();
        }
    }
    ngOnDestroy() {
        this.isDestroy = true;
        this.destroy();
    }
    ngOnChanges(changes) {
        if (!changes.config.firstChange) {
            this.restart();
        }
    }
    static { this.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "17.0.8", ngImport: i0, type: CountdownComponent, deps: [{ token: LOCALE_ID }, { token: i1.CountdownTimer }, { token: i0.ChangeDetectorRef }, { token: i0.NgZone }, { token: COUNTDOWN_CONFIG, optional: true }], target: i0.ɵɵFactoryTarget.Component }); }
    static { this.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "17.0.0", version: "17.0.8", type: CountdownComponent, isStandalone: true, selector: "countdown", inputs: { config: "config", render: "render" }, outputs: { event: "event" }, host: { properties: { "class.count-down": "true" } }, providers: [CountdownTimer], usesOnChanges: true, ngImport: i0, template: `
    @if (render) {
    <ng-container *ngTemplateOutlet="render; context: { $implicit: i }" />
    } @else {
    <span [innerHTML]="i.text"></span>
    }
  `, isInline: true, styles: [".count-down{font-variant-numeric:tabular-nums}\n"], dependencies: [{ kind: "directive", type: NgTemplateOutlet, selector: "[ngTemplateOutlet]", inputs: ["ngTemplateOutletContext", "ngTemplateOutlet", "ngTemplateOutletInjector"] }], changeDetection: i0.ChangeDetectionStrategy.OnPush, encapsulation: i0.ViewEncapsulation.None }); }
}
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "17.0.8", ngImport: i0, type: CountdownComponent, decorators: [{
            type: Component,
            args: [{ selector: 'countdown', template: `
    @if (render) {
    <ng-container *ngTemplateOutlet="render; context: { $implicit: i }" />
    } @else {
    <span [innerHTML]="i.text"></span>
    }
  `, host: { '[class.count-down]': 'true' }, encapsulation: ViewEncapsulation.None, changeDetection: ChangeDetectionStrategy.OnPush, imports: [NgTemplateOutlet], providers: [CountdownTimer], standalone: true, styles: [".count-down{font-variant-numeric:tabular-nums}\n"] }]
        }], ctorParameters: () => [{ type: undefined, decorators: [{
                    type: Inject,
                    args: [LOCALE_ID]
                }] }, { type: i1.CountdownTimer }, { type: i0.ChangeDetectorRef }, { type: i0.NgZone }, { type: undefined, decorators: [{
                    type: Optional
                }, {
                    type: Inject,
                    args: [COUNTDOWN_CONFIG]
                }] }], propDecorators: { config: [{
                type: Input,
                args: [{ required: true }]
            }], render: [{
                type: Input
            }], event: [{
                type: Output
            }] } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY291bnRkb3duLmNvbXBvbmVudC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uL2xpYi9zcmMvY291bnRkb3duLmNvbXBvbmVudC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQ0wsU0FBUyxFQUNULEtBQUssRUFJTCxNQUFNLEVBQ04sWUFBWSxFQUdaLHVCQUF1QixFQUN2QixpQkFBaUIsRUFDakIsTUFBTSxFQUNOLFNBQVMsRUFJVCxRQUFRLEdBQ1QsTUFBTSxlQUFlLENBQUM7QUFFdkIsT0FBTyxFQUFtQixlQUFlLEVBQXVELE1BQU0sY0FBYyxDQUFDO0FBQ3JILE9BQU8sRUFBRSxjQUFjLEVBQUUsTUFBTSxtQkFBbUIsQ0FBQztBQUNuRCxPQUFPLEVBQUUsZ0JBQWdCLEVBQUUsVUFBVSxFQUFFLE1BQU0saUJBQWlCLENBQUM7QUFDL0QsT0FBTyxFQUFFLGdCQUFnQixFQUFFLE1BQU0sV0FBVyxDQUFDOzs7QUF5QjdDLE1BQU0sT0FBTyxrQkFBa0I7SUFTN0IsSUFDSSxNQUFNLENBQUMsQ0FBa0I7UUFDM0IsSUFBSSxDQUFDLENBQUMsTUFBTSxJQUFJLElBQUksSUFBSSxDQUFDLEtBQUssQ0FBQyxPQUFPLENBQUMsQ0FBQyxDQUFDLE1BQU0sQ0FBQyxJQUFJLENBQUMsQ0FBQyxNQUFNLEdBQUcsQ0FBQyxFQUFFO1lBQ2hFLENBQUMsQ0FBQyxNQUFNLEdBQUcsQ0FBQyxDQUFDLENBQUMsTUFBTSxDQUFDLENBQUM7U0FDdkI7UUFDRCxJQUFJLENBQUMsT0FBTyxHQUFHLENBQUMsQ0FBQztJQUNuQixDQUFDO0lBQ0QsSUFBSSxNQUFNO1FBQ1IsT0FBTyxJQUFJLENBQUMsT0FBTyxDQUFDO0lBQ3RCLENBQUM7SUFJRCxZQUM2QixNQUFjLEVBQ2pDLEtBQXFCLEVBQ3JCLEdBQXNCLEVBQ3RCLE1BQWMsRUFDd0IsTUFBd0I7UUFKM0MsV0FBTSxHQUFOLE1BQU0sQ0FBUTtRQUNqQyxVQUFLLEdBQUwsS0FBSyxDQUFnQjtR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QUFNLEVBQUU7WUFDdkIsSUFBSSxDQUFDLEtBQUssRUFBRSxDQUFDO1NBQ2Q7SUFDSCxDQUFDO0lBRUQsV0FBVztRQUNULElBQUksQ0FBQyxTQUFTLEdBQUcsSUFBSSxDQUFDO1FBQ3RCLElBQUksQ0FBQyxPQUFPLEVBQUUsQ0FBQztJQUNqQixDQUFDO0lBRUQsV0FBVyxDQUFDLE9BQTZEO1FBQ3ZFLElBQUksQ0FBQyxPQUFPLENBQUMsTUFBTyxDQUFDLFdBQVcsRUFBRTtZQUNoQyxJQUFJLENBQUMsT0FBTyxFQUFFLENBQUM7U0FDaEI7SUFDSCxDQUFDOzhHQTFNVSxrQkFBa0Isa0JBdUJuQixTQUFTLGtHQUlHLGdCQUFnQjtrR0EzQjNCLGtCQUFrQiwyTEFIbEIsQ0FBQyxjQUFjLENBQUMsK0NBbEJqQjs7Ozs7O0dBTVQsMEhBV1MsZ0JBQWdCOzsyRkFJZixrQkFBa0I7a0JBdkI5QixTQUFTOytCQUNFLFdBQVcsWUFDWDs7Ozs7O0dBTVQsUUFDSyxFQUFFLG9CQUFvQixFQUFFLE1BQU0sRUFBRSxpQkFRdkIsaUJBQWlCLENBQUMsSUFBSSxtQkFDcEIsdUJBQXVCLENBQUMsTUFBTSxXQUN0QyxDQUFDLGdCQUFnQixDQUFDLGFBQ2hCLENBQUMsY0FBYyxDQUFDLGNBQ2YsSUFBSTs7MEJBeUJiLE1BQU07MkJBQUMsU0FBUzs7MEJBSWhCLFFBQVE7OzBCQUFJLE1BQU07MkJBQUMsZ0JBQWdCO3lDQWpCbEMsTUFBTTtzQkFEVCxLQUFLO3VCQUFDLEVBQUUsUUFBUSxFQUFFLElBQUksRUFBRTtnQkFVaEIsTUFBTTtzQkFBZCxLQUFLO2dCQUNhLEtBQUs7c0JBQXZCLE1BQU0iLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQge1xuICBDb21wb25lbnQsXG4gIElucHV0LFxuICBPbkNoYW5nZXMsXG4gIFNpbXBsZUNoYW5nZXMsXG4gIE9uRGVzdHJveSxcbiAgT3V0cHV0LFxuICBFdmVudEVtaXR0ZXIsXG4gIE9uSW5pdCxcbiAgU2ltcGxlQ2hhbmdlLFxuICBDaGFuZ2VEZXRlY3Rpb25TdHJhdGVneSxcbiAgVmlld0VuY2Fwc3VsYXRpb24sXG4gIEluamVjdCxcbiAgTE9DQUxFX0lELFxuICBDaGFuZ2VEZXRlY3RvclJlZixcbiAgVGVtcGxhdGVSZWYsXG4gIE5nWm9uZSxcbiAgT3B0aW9uYWwsXG59IGZyb20gJ0Bhbmd1bGFyL2NvcmUnO1xuXG5pbXBvcnQgeyBDb3VudGRvd25Db25maWcsIENvdW50ZG93blN0YXR1cywgQ291bnRkb3duRXZlbnQsIENvdW50ZG93bkV2ZW50QWN0aW9uLCBDb3VudGRvd25JdGVtIH0gZnJvbSAnLi9pbnRlcmZhY2VzJztcbmltcG9ydCB7IENvdW50ZG93blRpbWVyIH0gZnJvbSAnLi9jb3VudGRvd24udGltZXInO1xuaW1wb3J0IHsgTmdUZW1wbGF0ZU91dGxldCwgZm9ybWF0RGF0ZSB9IGZyb20gJ0Bhbmd1bGFyL2NvbW1vbic7XG5pbXBvcnQgeyBDT1VOVERPV05fQ09ORklHIH0gZnJvbSAnLi9wcm92aWRlJztcblxuQENvbXBvbmVudCh7XG4gIHNlbGVjdG9yOiAnY291bnRkb3duJyxcbiAgdGVtcGxhdGU6IGBcbiAgICBAaWYgKHJlbmRlcikge1xuICAgIDxuZy1jb250YWluZXIgKm5nVGVtcGxhdGVPdXRsZXQ9XCJyZW5kZXI7IGNvbnRleHQ6IHsgJGltcGxpY2l0OiBpIH1cIiAvPlxuICAgIH0gQGVsc2Uge1xuICAgIDxzcGFuIFtpbm5lckhUTUxdPVwiaS50ZXh0XCI+PC9zcGFuPlxuICAgIH1cbiAgYCxcbiAgaG9zdDogeyAnW2NsYXNzLmNvdW50LWRvd25dJzogJ3RydWUnIH0sXG4gIHN0eWxlczogW1xuICAgIGBcbiAgICAgIC5jb3VudC1kb3duIHtcbiAgICAgICAgZm9udC12YXJpYW50LW51bWVyaWM6IHRhYnVsYXItbnVtcztcbiAgICAgIH1cbiAgICBgLFxuICBdLFxuICBlbmNhcHN1bGF0aW9uOiBWaWV3RW5jYXBzdWxhdGlvbi5Ob25lLFxuICBjaGFuZ2VEZXRlY3Rpb246IENoYW5nZURldGVjdGlvblN0cmF0ZWd5Lk9uUHVzaCxcbiAgaW1wb3J0czogW05nVGVtcGxhdGVPdXRsZXRdLFxuICBwcm92aWRlcnM6IFtDb3VudGRvd25UaW1lcl0sXG4gIHN0YW5kYWxvbmU6IHRydWUsXG59KVxuZXhwb3J0IGNsYXNzIENvdW50ZG93bkNvbXBvbmVudCBpbXBsZW1lbnRzIE9uSW5pdCwgT25DaGFuZ2VzLCBPbkRlc3Ryb3kge1xuICBwcml2YXRlIGZyZXF1ZW5jeSA9IDEwMDA7XG4gIHByaXZhdGUgX25vdGlmeTogeyBba2V5OiBudW1iZXJdOiBib29sZWFuIH0gPSB7fTtcbiAgcHJpdmF0ZSBzdGF0dXM6IENvdW50ZG93blN0YXR1cyA9IENvdW50ZG93blN0YXR1cy5pbmc7XG4gIHByaXZhdGUgaXNEZXN0cm95ID0gZmFsc2U7XG4gIHByaXZhdGUgX2NvbmZpZyE6IENvdW50ZG93bkNvbmZpZztcbiAgaTogQ291bnRkb3duSXRlbSA9IHt9O1xuICBsZWZ0ID0gMDtcblxuICBASW5wdXQoeyByZXF1aXJlZDogdHJ1ZSB9KVxuICBzZXQgY29uZmlnKGk6IENvdW50ZG93bkNvbmZpZykge1xuICAgIGlmIChpLm5vdGlmeSAhPSBudWxsICYmICFBcnJheS5pc0FycmF5KGkubm90aWZ5KSAmJiBpLm5vdGlmeSA+IDApIHtcbiAgICAgIGkubm90aWZ5ID0gW2kubm90aWZ5XTtcbiAgICB9XG4gICAgdGhpcy5fY29uZmlnID0gaTtcbiAgfVxuICBnZXQgY29uZmlnKCk6IENvdW50ZG93bkNvbmZpZyB7XG4gICAgcmV0dXJuIHRoaXMuX2NvbmZpZztcbiAgfVxuICBASW5wdXQoKSByZW5kZXI/OiBUZW1wbGF0ZVJlZjx7ICRpbXBsaWNpdDogQ291bnRkb3duSXRlbSB9PjtcbiAgQE91dHB1dCgpIHJlYWRvbmx5IGV2ZW50ID0gbmV3IEV2ZW50RW1pdHRlcjxDb3VudGRvd25FdmVudD4oKTtcblxuICBjb25zdHJ1Y3RvcihcbiAgICBASW5qZWN0KExPQ0FMRV9JRCkgcHJpdmF0ZSBsb2NhbGU6IHN0cmluZyxcbiAgICBwcml2YXRlIHRpbWVyOiBDb3VudGRvd25UaW1lcixcbiAgICBwcml2YXRlIGNkcjogQ2hhbmdlRGV0ZWN0b3JSZWYsXG4gICAgcHJpdmF0ZSBuZ1pvbmU6IE5nWm9uZSxcbiAgICBAT3B0aW9uYWwoKSBASW5qZWN0KENPVU5URE9XTl9DT05GSUcpIHByaXZhdGUgZGVmQ29nPzogQ291bnRkb3duQ29uZmlnLFxuICApIHt9XG5cbiAgLyoqXG4gICAqIFN0YXJ0IGNvdW50ZG93biwgeW91IG11c3QgbWFudWFsbHkgY2FsbCB3aGVuIGBkZW1hbmQ6IGZhbHNlYFxuICAgKi9cbiAgYmVnaW4oKTogdm9pZCB7XG4gICAgdGhpcy5zdGF0dXMgPSBDb3VudGRvd25TdGF0dXMuaW5nO1xuICAgIHRoaXMuY2FsbEV2ZW50KCdzdGFydCcpO1xuICB9XG5cbiAgLyoqXG4gICAqIFJlc3RhcnQgY291bnRkb3duXG4gICAqL1xuICByZXN0YXJ0KCk6IHZvaWQge1xuICAgIGlmICh0aGlzLnN0YXR1cyAhPT0gQ291bnRkb3duU3RhdHVzLnN0b3ApIHtcbiAgICAgIHRoaXMuZGVzdHJveSgpO1xuICAgIH1cbiAgICB0aGlzLmluaXQoKTtcbiAgICB0aGlzLmNhbGxFdmVudCgncmVzdGFydCcpO1xuICB9XG5cbiAgLyoqXG4gICAqIFN0b3AgY291bnRkb3duLCBtdXN0IGNhbGwgYHJlc3RhcnRgIHdoZW4gc3RvcHBlZCwgaXQncyBkaWZmZXJlbnQgZnJvbSBwYXVzZSwgdW5hYmxlIHRvIHJlY292ZXJcbiAgICovXG4gIHN0b3AoKTogdm9pZCB7XG4gICAgaWYgKHRoaXMuc3RhdHVzID09PSBDb3VudGRvd25TdGF0dXMuc3RvcCkge1xuICAgICAgcmV0dXJuO1xuICAgIH1cbiAgICB0aGlzLnN0YXR1cyA9IENvdW50ZG93blN0YXR1cy5zdG9wO1xuICAgIHRoaXMuZGVzdHJveSgpO1xuICAgIHRoaXMuY2FsbEV2ZW50KCdzdG9wJyk7XG4gIH1cblxuICAvKipcbiAgICogUGF1c2UgY291bnRkb3duLCB5b3UgY2FuIHVzZSBgcmVzdW1lYCB0byByZWNvdmVyIGFnYWluXG4gICAqL1xuICBwYXVzZSgpOiB2b2lkIHtcbiAgICBpZiAodGhpcy5zdGF0dXMgPT09IENvdW50ZG93blN0YXR1cy5zdG9wIHx8IHRoaXMuc3RhdHVzID09PSBDb3VudGRvd25TdGF0dXMucGF1c2UpIHtcbiAgICAgIHJldHVybjtcbiAgICB9XG4gICAgdGhpcy5zdGF0dXMgPSBDb3VudGRvd25TdGF0dXMucGF1c2U7XG4gICAgdGhpcy5jYWxsRXZlbnQoJ3BhdXNlJyk7XG4gIH1cblxuICAvKipcbiAgICogUmVzdW1lIGNvdW50ZG93blxuICAgKi9cbiAgcmVzdW1lKCk6IHZvaWQge1xuICAgIGlmICh0aGlzLnN0YXR1cyA9PT0gQ291bnRkb3duU3RhdHVzLnN0b3AgfHwgdGhpcy5zdGF0dXMgIT09IENvdW50ZG93blN0YXR1cy5wYXVzZSkge1xuICAgICAgcmV0dXJuO1xuICAgIH1cbiAgICB0aGlzLnN0YXR1cyA9IENvdW50ZG93blN0YXR1cy5pbmc7XG4gICAgdGhpcy5jYWxsRXZlbnQoJ3Jlc3VtZScpO1xuICB9XG5cbiAgcHJpdmF0ZSBjYWxsRXZlbnQoYWN0aW9uOiBDb3VudGRvd25FdmVudEFjdGlvbik6IHZvaWQge1xuICAgIHRoaXMuZXZlbnQuZW1pdCh7IGFjdGlvbiwgbGVmdDogdGhpcy5sZWZ0LCBzdGF0dXM6IHRoaXMuc3RhdHVzLCB0ZXh0OiB0aGlzLmkudGV4dCEgfSk7XG4gIH1cblxuICBwcml2YXRlIGluaXQoKTogdm9pZCB7XG4gICAgY29uc3QgY29uZmlnID0gKHRoaXMuY29uZmlnID0ge1xuICAgICAgZGVtYW5kOiBmYWxzZSxcbiAgICAgIGxlZnRUaW1lOiAwLFxuICAgICAgZm9ybWF0OiAnSEg6bW06c3MnLFxuICAgICAgdGltZXpvbmU6ICcrMDAwMCcsXG4gICAgICBmb3JtYXREYXRlOiAoeyBkYXRlLCBmb3JtYXRTdHIsIHRpbWV6b25lIH0pID0+IHtcbiAgICAgICAgcmV0dXJuIGZvcm1hdERhdGUobmV3IERhdGUoZGF0ZSksIGZvcm1hdFN0ciwgdGhpcy5sb2NhbGUsIHRpbWV6b25lIHx8ICcrMDAwMCcpO1xuICAgICAgfSxcbiAgICAgIC4uLnRoaXMuZGVmQ29nLFxuICAgICAgLi4udGhpcy5jb25maWcsXG4gICAgfSk7XG4gICAgY29uc3QgZnJxID0gKHRoaXMuZnJlcXVlbmN5ID0gfmNvbmZpZy5mb3JtYXQhLmluZGV4T2YoJ1MnKSA/IDEwMCA6IDEwMDApO1xuICAgIHRoaXMuc3RhdHVzID0gY29uZmlnLmRlbWFuZCA/IENvdW50ZG93blN0YXR1cy5wYXVzZSA6IENvdW50ZG93blN0YXR1cy5pbmc7XG5cbiAgICB0aGlzLmdldExlZnQoKTtcblxuICAgIC8vIGJpbmQgcmVmbG93IHRvIG1lXG4gICAgY29uc3QgX3JlZmxvdyA9IHRoaXMucmVmbG93O1xuICAgIHRoaXMucmVmbG93ID0gKGNvdW50OiBudW1iZXIgPSAwLCBmb3JjZTogYm9vbGVhbiA9IGZhbHNlKSA9PiBfcmVmbG93LmFwcGx5KHRoaXMsIFtjb3VudCwgZm9yY2VdKTtcblxuICAgIGlmIChBcnJheS5pc0FycmF5KGNvbmZpZy5ub3RpZnkpKSB7XG4gICAgICBjb25maWcubm90aWZ5LmZvckVhY2goKHRpbWU6IG51bWJlcikgPT4ge1xuICAgICAgICBpZiAodGltZSA8IDEpIHtcbiAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoYFRoZSBub3RpZnkgY29uZmlnIG11c3QgYmUgYSBwb3NpdGl2ZSBpbnRlZ2VyLmApO1xuICAgICAgICB9XG5cbiAgICAgICAgdGltZSA9IHRpbWUgKiAxMDAwO1xuICAgICAgICB0aW1lID0gdGltZSAtICh0aW1lICUgZnJxKTtcbiAgICAgICAgdGhpcy5fbm90aWZ5W3RpbWVdID0gdHJ1ZTtcbiAgICAgIH0pO1xuICAgIH1cblxuICAgIHRoaXMudGltZXIuYWRkKHRoaXMucmVmbG93LCBmcnEpLnN0YXJ0KCk7XG5cbiAgICB0aGlzLnJlZmxvdygwLCB0cnVlKTtcbiAgfVxuXG4gIHByaXZhdGUgZGVzdHJveSgpOiB0aGlzIHtcbiAgICB0aGlzLnRpbWVyLnJlbW92ZSh0aGlzLnJlZmxvdyk7XG4gICAgcmV0dXJuIHRoaXM7XG4gIH1cblxuICAvKipcbiAgICog5pu05paw5pe26ZKfXG4gICAqL1xuICBwcml2YXRlIHJlZmxvdyhjb3VudDogbnVtYmVyID0gMCwgZm9yY2U6IGJvb2xlYW4gPSBmYWxzZSk6IHZvaWQge1xuICAgIGlmICh0aGlzLmlzRGVzdHJveSkge1xuICAgICAgcmV0dXJuO1xuICAgIH1cblxuICAgIGNvbnN0IHsgc3RhdHVzLCBjb25maWcsIF9ub3RpZnkgfSA9IHRoaXM7XG4gICAgaWYgKCFmb3JjZSAmJiBzdGF0dXMgIT09IENvdW50ZG93blN0YXR1cy5pbmcpIHtcbiAgICAgIHJldHVybjtcbiAgICB9XG5cbiAgICBsZXQgdmFsdWUgPSAodGhpcy5sZWZ0ID0gdGhpcy5sZWZ0IC0gdGhpcy5mcmVxdWVuY3kgKiBjb3VudCk7XG4gICAgaWYgKHZhbHVlIDwgMSkge1xuICAgICAgdmFsdWUgPSAwO1xuICAgIH1cbiAgICB0aGlzLmkgPSB7XG4gICAgICB2YWx1ZSxcbiAgICAgIHRleHQ6IGNvbmZpZy5mb3JtYXREYXRlISh7IGRhdGU6IHZhbHVlLCBmb3JtYXRTdHI6IGNvbmZpZy5mb3JtYXQhLCB0aW1lem9uZTogY29uZmlnLnRpbWV6b25lIH0pLFxuICAgIH07XG4gICAgaWYgKHR5cGVvZiBjb25maWcucHJldHR5VGV4dCA9PT0gJ2Z1bmN0aW9uJykge1xuICAgICAgdGhpcy5pLnRleHQgPSBjb25maWcucHJldHR5VGV4dCh0aGlzLmkudGV4dCEpO1xuICAgIH1cbiAgICB0aGlzLmNkci5kZXRlY3RDaGFuZ2VzKCk7XG5cbiAgICBpZiAoY29uZmlnLm5vdGlmeSA9PT0gMCB8fCBfbm90aWZ5W3ZhbHVlXSkge1xuICAgICAgdGhpcy5uZ1pvbmUucnVuKCgpID0+IHtcbiAgICAgICAgdGhpcy5jYWxsRXZlbnQoJ25vdGlmeScpO1xuICAgICAgfSk7XG4gICAgfVxuXG4gICAgaWYgKHZhbHVlID09PSAwKSB7XG4gICAgICB0aGlzLm5nWm9uZS5ydW4oKCkgPT4ge1xuICAgICAgICB0aGlzLnN0YXR1cyA9IENvdW50ZG93blN0YXR1cy5kb25lO1xuICAgICAgICB0aGlzLmRlc3Ryb3koKTtcbiAgICAgICAgdGhpcy5jYWxsRXZlbnQoJ2RvbmUnKTtcbiAgICAgIH0pO1xuICAgIH1cbiAgfVxuXG4gIC8qKlxuICAgKiDojrflj5blgJLorqHml7bliankvZnluKfmlbBcbiAgICovXG4gIHByaXZhdGUgZ2V0TGVmdCgpOiB2b2lkIHtcbiAgICBjb25zdCB7IGNvbmZpZywgZnJlcXVlbmN5IH0gPSB0aGlzO1xuICAgIGxldCBsZWZ0ID0gY29uZmlnLmxlZnRUaW1lISAqIDEwMDA7XG4gICAgY29uc3QgZW5kID0gY29uZmlnLnN0b3BUaW1lO1xuXG4gICAgaWYgKCFsZWZ0ICYmIGVuZCkge1xuICAgICAgbGVmdCA9IGVuZCAtIG5ldyBEYXRlKCkuZ2V0VGltZSgpO1xuICAgIH1cblxuICAgIHRoaXMubGVmdCA9IGxlZnQgLSAobGVmdCAlIGZyZXF1ZW5jeSk7XG4gIH1cblxuICBuZ09uSW5pdCgpOiB2b2lkIHtcbiAgICB0aGlzLmluaXQoKTtcbiAgICBpZiAoIXRoaXMuY29uZmlnLmRlbWFuZCkge1xuICAgICAgdGhpcy5iZWdpbigpO1xuICAgIH1cbiAgfVxuXG4gIG5nT25EZXN0cm95KCk6IHZvaWQge1xuICAgIHRoaXMuaXNEZXN0cm95ID0gdHJ1ZTtcbiAgICB0aGlzLmRlc3Ryb3koKTtcbiAgfVxuXG4gIG5nT25DaGFuZ2VzKGNoYW5nZXM6IHsgW1AgaW4ga2V5b2YgdGhpc10/OiBTaW1wbGVDaGFuZ2UgfSAmIFNpbXBsZUNoYW5nZXMpOiB2b2lkIHtcbiAgICBpZiAoIWNoYW5nZXMuY29uZmlnIS5maXJzdENoYW5nZSkge1xuICAgICAgdGhpcy5yZXN0YXJ0KCk7XG4gICAgfVxuICB9XG59XG4iXX0=