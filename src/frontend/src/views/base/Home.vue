<template>


    <div class="card">
        <div class="flex items-center">
            <div class="font-semibold text-xl mr-4">Bem-vindo ao {{ appName }}</div>
            <img src="/icon.svg" width="70px">
        </div>

        <div class="mt-4 ">
            <p>{{ appName }} is a tool that collect and analyze <b>DevOps</b> Metrics. Trying to assess the
                maturity of DevOps adoption</p>

            <div class="mt-4 card text-ajuda">
                <div class="mb-4">This tool is part of a doctoral thesis and is still under development.</div>
                <div class="mb-4">Therefore, there are no guarantees about its perfect functioning; the methodology
                    contained in it is under evaluation, still without any scientific proof of its efficiency..
                </div>
            </div>
        </div>


        <div class="mt-5 card text-ajuda">
            <div class="font-semibold text-xl mb-4">License</div>

            <div>
                <p>Copyright (c) 2023 Jadson Santos - jadson.santos@ufrn.br</p>
                <p>
                    Permission is hereby granted, free of charge, to any person obtaining a copy
                    of this software and associated documentation files (the "Software"), to deal
                    in the Software without restriction, including without limitation the rights
                    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                    copies of the Software, and to permit persons to whom the Software is
                    furnished to do so, subject to the following conditions:
                </p>
                <p>
                    The above copyright notice and this permission notice shall be included in
                    all copies or substantial portions of the Software.
                </p>
                <p>
                    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
                    THE SOFTWARE.
                </p>
            </div>
        </div>
    </div>

</template>


<script setup>
import { useLayout } from '@/layout/composables/layout';
import { onMounted, ref, watch } from 'vue';

const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '').toUpperCase()



const { getPrimary, getSurface, isDarkTheme } = useLayout();

onMounted(() => {
    setColorOptions();
});


const setColorOptions = () => {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

};


watch(
    [getPrimary, getSurface, isDarkTheme],
    () => {
        setColorOptions();
    },
    { immediate: true }
);
</script>
