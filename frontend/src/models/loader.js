export class Loader {
    constructor(callback, progressFunction, referencedTime) {
        this.callback = callback;
        this.progressFunction = progressFunction;
        this.referencedTime = referencedTime;

        this.msInLnTime = 60.708/this.referencedTime;
        this.newMsInLnTime = 0;
        this.breakTime = 0;

        this.progress = 0;
        this.startTime = -1;
        this.lastUpdate = -1;
        this.canEnd = false;
        this.ended = false;
    }

    get currentX() {
        return ((!this.breakTime ? Date.now() : this.breakTime) - this.startTime) * this.msInLnTime +
                (!this.breakTime ? 0 : (Date.now() - this.breakTime) * this.newMsInLnTime);
    }

    fromLnTime(logTime) {
        return this.squareFunction(logTime) < this.lnFunction(logTime) ? this.squareFunction(logTime) : this.lnFunction(logTime);
    }

    percentFromLnTime(logTime) {
        return this.fromLnTime(logTime) / (Math.E * 4);
    }

    squareFunction(x) {
        return Math.sqrt(1.5 * x);
    }

    lnFunction(x) {
        return Math.log(x) + 2 * Math.E;
    }

    finishFunction(x) {
        return (Math.pow(x + this.offsetX, 1.5) + this.offsetY) / (Math.E * 4);
    }

    reverseFinishFunction(y) {
        return Math.pow(Math.E * 4 * y - this.offsetY, 2/3) - this.offsetX
    }

    tick() {
        if(this.ended) return;
        let percent = this.canEnd ?
                        this.finishFunction(this.currentX) :
                        this.percentFromLnTime(this.currentX);
        if(percent >= 1 && this.canEnd) {
            percent = 1;
            this.ended = true;
            return this.callback(this.data);
        } else {
            setTimeout(this.tick.bind(this), 10);
            this.progressFunction(Math.max(Math.min(percent, 1),0)*100);
        }
    }

    end(data = null) {
        this.canEnd = true;
        this.data = data;
        if(this.startTime < 0) {
            this.ended = true;
            return this.callback(data);
        }
        this.offsetX = -this.currentX;
        this.offsetY = this.fromLnTime(this.currentX);
        console.log(this.reverseFinishFunction(1))
        console.log(this.currentX)
        console.log(this.msInLnTime)
        let remainingTime = (this.reverseFinishFunction(1) - this.currentX) / this.msInLnTime;
        if(remainingTime > 1500) {
            this.newMsInLnTime = remainingTime / 1500 * this.msInLnTime;
            this.breakTime = Date.now();
        }
        console.log(remainingTime);
    }

    start() {
        if(this.canEnd) return;
        this.startTime = Date.now();
        this.lastUpdate = this.startTime;
        setTimeout(this.tick.bind(this), 10);
    }
}